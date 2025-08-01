package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.PropertiesUtils;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildTable {
    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);
    private static final String SQL_SHOW_TABLE_STATUS = "show table status";
    private static final String SQL_SHOW_TABLE_FIELDS = "show full fields from %s";
    private static final String SQL_SHOW_TABLE_INDEX = "show index from %s";
    private static Connection conn = null;

    static {
        String driverName = PropertiesUtils.getString("db.driver.name");
        String url = PropertiesUtils.getString("db.url");
        String user = PropertiesUtils.getString("db.username");
        String password = PropertiesUtils.getString("db.password");
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            logger.error("数据库连接失败", e);
        }
    }

    /**
     * 设置表格信息
     */
    public static List<TableInfo> getTables() {
        PreparedStatement ps = null;
        ResultSet tableResult = null;

        List<TableInfo> tableInfoList = new ArrayList();
        try {
            ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
            tableResult = ps.executeQuery();
            // 对于每一张表
            while (tableResult.next()) {
                String tableName = tableResult.getString("name");
                String comment = tableResult.getString("comment");
                // logger.info("tableName: {}, comment: {}", tableName, comment);

                String beanName = tableName;
                if (Constants.IGNORE_TABLE_PREFIX) {
                    beanName = tableName.substring(beanName.indexOf("_") + 1);
                }
                beanName = proccessFiled(beanName, true);

                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(comment);
                tableInfo.setBeanParamName(beanName + Constants.SUFFIX_BEAN_QUERY);

                // 读取并设置每一张表的字段信息
                readFieldInfo(tableInfo);
                // 读取并设置每一张表的索引信息
                getKeyIndexInfo(tableInfo);
                // logger.info("tableinfo:{}", JsonUtils.converOb2Json(tableInfo));
                tableInfoList.add(tableInfo);
            }
        } catch (Exception e) {
            logger.error("读取表失败", e);
        } finally {
            if (tableResult != null) {
                try {
                    tableResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return tableInfoList;
    }

    // 读取并设置每张表的字段信息
    private static void readFieldInfo(TableInfo tableinfo) {
        PreparedStatement ps = null;
        ResultSet fieldResult = null;

        // 一张表有多个字段
        List<FieldInfo> fieldInfoList = new ArrayList();

        // 扩展字段
        List<FieldInfo> extendFieldList = new ArrayList();

        try {
            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_FIELDS, tableinfo.getTableName()));
            fieldResult = ps.executeQuery();
            // 对于每个字段
            while (fieldResult.next()) {
                String field = fieldResult.getString("field");
                String type = fieldResult.getString("type");
                String extra = fieldResult.getString("extra");
                String comment = fieldResult.getString("comment");

                if (type.indexOf("(") > 0) {
                    type = type.substring(0, type.indexOf("("));
                }

                String propertyName = proccessFiled(field, false);

                FieldInfo fieldInfo = new FieldInfo();
                fieldInfoList.add(fieldInfo);

                fieldInfo.setFieldName(field);
                fieldInfo.setComment(comment);
                fieldInfo.setSqlType(type);
                fieldInfo.setAutoIncrement("auto_increment".equalsIgnoreCase(extra));
                fieldInfo.setPropertyName(propertyName);
                fieldInfo.setJavaType(processJavaType(type));

                // 补充设置表信息，注意：或运算是自己加的，
                // 原视频只有 || 后的内容，因为发现按照原思路会在每一次读取字段信息均会修改这几个表信息（即若最后不为这些类型则始终为 false）
                // 同时发现一开始定义的时候定义成了 Boolean（默认值为 null），改成 boolean（默认值为 false）
                tableinfo.setHaveDateTime(
                    tableinfo.getHaveDateTime() || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type));
                tableinfo.setHaveDate(tableinfo.getHaveDate() || ArrayUtils.contains(Constants.SQL_DATE_TYPES, type));
                tableinfo.setHaveBigDecimal(
                    tableinfo.getHaveBigDecimal() || ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type));

                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, type)) {
                    FieldInfo fuzzyField = new FieldInfo();
                    fuzzyField.setJavaType(fieldInfo.getJavaType());
                    fuzzyField.setPropertyName(propertyName + Constants.SUFFIX_BEAN_QUERY_FUZZY);
                    fuzzyField.setFieldName(fieldInfo.getFieldName());
                    fuzzyField.setSqlType(type);
                    extendFieldList.add(fuzzyField);
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, type) || ArrayUtils.contains(
                    Constants.SQL_DATE_TIME_TYPES, type)) {
                    FieldInfo timeStartField = new FieldInfo();
                    timeStartField.setJavaType("String");
                    timeStartField.setPropertyName(propertyName + Constants.SUFFIX_BEAN_QUERY_TIME_START);
                    timeStartField.setFieldName(fieldInfo.getFieldName());
                    timeStartField.setSqlType(type);
                    extendFieldList.add(timeStartField);

                    FieldInfo timeEndField = new FieldInfo();
                    timeEndField.setJavaType("String");
                    timeEndField.setPropertyName(propertyName + Constants.SUFFIX_BEAN_QUERY_TIME_END);
                    timeEndField.setFieldName(fieldInfo.getFieldName());
                    timeEndField.setSqlType(type);
                    extendFieldList.add(timeEndField);
                }

            }
            // logger.info("tableInfo:{}", JsonUtils.converOb2Json(tableinfo));
            tableinfo.setFieldList(fieldInfoList);
            tableinfo.setExtendFieldList(extendFieldList);
        } catch (Exception e) {
            logger.error("读取表失败", e);
        } finally {
            if (fieldResult != null) {
                try {
                    fieldResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // 设置表格索引信息
    private static void getKeyIndexInfo(TableInfo tableinfo) {
        PreparedStatement ps = null;
        ResultSet fieldResult = null;

        List<FieldInfo> fieldInfoList = new ArrayList();
        try {
            Map<String, FieldInfo> tempMap = new HashMap();
            for (FieldInfo fieldInfo : tableinfo.getFieldList()) {
                tempMap.put(fieldInfo.getFieldName(), fieldInfo);
            }

            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_INDEX, tableinfo.getTableName()));
            fieldResult = ps.executeQuery();
            // 对于每一行索引（可以先去数据库试试 SQL_SHOW_TABLE_INDEX 的查询结果）
            while (fieldResult.next()) {
                String keyName = fieldResult.getString("key_name");
                Integer nonUnique = fieldResult.getInt("non_unique");
                String columnName = fieldResult.getString("column_name");

                // 只需要唯一索引
                if (nonUnique == 1) {
                    continue;
                }
                // 对于每个索引定义一个列表，因为每个索引可能包含多个字段
                List<FieldInfo> keyFieldList = tableinfo.getKeyIndexMap().get(keyName);
                if (keyFieldList == null) {
                    // 对于新的索引名肯定是空的，所以 new 一个
                    keyFieldList = new ArrayList();
                    // 此处直接设置 keyIndexMap 了，没有用到 setter。
                    // 这里是否可以避免使用 getter 从而就能改变信息？因为违反了封装性
                    tableinfo.getKeyIndexMap().put(keyName, keyFieldList);
                }

                // for (FieldInfo fieldInfo : tableinfo.getFieldList()) {
                //     if (fieldInfo.getFieldName().equals(columnName)) {
                //         keyFieldList.add(fieldInfo);
                //     }
                // }
                // 对于表中的每个字段，若为索引，则将该字段信息放入该索引对应的列表中，
                // 最开始使用了一个 Map，避免了上述循环的问题
                keyFieldList.add(tempMap.get(columnName));
            }
        } catch (Exception e) {
            logger.error("读取索引失败", e);
        } finally {
            if (fieldResult != null) {
                try {
                    fieldResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static String proccessFiled(String filed, Boolean upperCaseFirsrLetter) {
        StringBuffer sb = new StringBuffer();
        String[] fields = filed.split("_");
        sb.append(upperCaseFirsrLetter ? StringUtils.upperCaseFirsrLetter(fields[0]) : fields[0]);
        for (int i = 1, len = fields.length; i < len; i++) {
            sb.append(StringUtils.upperCaseFirsrLetter(fields[i]));
        }
        return sb.toString();
    }

    // 目前有个问题就是数据库中没有 Boolean 类型，一般都是用 tinyint(1) 代替，此时无法转换成 Java 的 Boolean 类型
    private static String processJavaType(String type) {
        if (ArrayUtils.contains(Constants.SQL_INTEGER_TYPES, type)) {
            return "Integer";
        } else if (ArrayUtils.contains(Constants.SQL_LONG_TYPES, type)) {
            return "Long";
        } else if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, type)) {
            return "String";
        } else if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type) || ArrayUtils.contains(
            Constants.SQL_DATE_TYPES, type)) {
            return "Date";
        } else if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type)) {
            return "BigDecimal";
        } else {
            throw new RuntimeException("无法识别的类型:" + type);
        }
    }

}
