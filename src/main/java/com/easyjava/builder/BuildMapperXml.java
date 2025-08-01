package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildMapperXml {
    private static final Logger logger = LoggerFactory.getLogger(BuildMapperXml.class);

    private static final String BASE_COLUMN_LIST = "base_column_list";
    private static final String BASE_QUERY_CONDITION = "base_query_condition";
    private static final String BASE_QUERY_CONDITION_EXTEND = "base_query_condition_extend";
    private static final String QUERY_CONDITION = "query_condition";

    public static void execute(TableInfo tableinfo) {
        File folder = new File(Constants.PATH_MAPPERS_XMLS);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String className = tableinfo.getBeanName() + Constants.SUFFIX_MAPPERS;
        File poFile = new File(folder, className + ".xml");

        OutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            osw = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(osw);
            bw.write(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<!DOCTYPE mapper\n" + "        PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n" + "        \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
            bw.write("<mapper namespace=\"" + Constants.PACKAGE_MAPPERS + "." + className + "\">");
            bw.newLine();

            // 实体映射
            bw.write("    <!--实体映射-->");
            bw.newLine();
            String poClass = Constants.PACKAGE_PO + "." + tableinfo.getBeanName();
            bw.write("    <resultMap id=\"base_result_map\" type=\"" + poClass + "\">");
            bw.newLine();
            // 拿到表里的 id 即主键
            // FieldInfo idField = null;
            List<String> idFieldList = new ArrayList<>();
            Map<String, List<FieldInfo>> keyIndexMap = tableinfo.getKeyIndexMap();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                if ("PRIMARY".equals(entry.getKey())) {
                    List<FieldInfo> fieldInfoList = entry.getValue();
                    if (fieldInfoList.size() == 1) {
                        String idField = fieldInfoList.get(0).getPropertyName();
                        idFieldList.add(idField);
                    }
                }
            }
            for (FieldInfo fieldInfo : tableinfo.getFieldList()) {
                bw.write("        <!--" + fieldInfo.getComment() + "-->");
                bw.newLine();
                String key = "";
                if ((!idFieldList.isEmpty()) && idFieldList.contains(fieldInfo.getPropertyName())) {
                    key = "id";
                } else {
                    key = "result";
                }
                bw.write(
                        "        <" + key + " column=\"" + fieldInfo.getFieldName() + "\" property=\"" + fieldInfo.getPropertyName() + "\"/>");
                bw.newLine();
            }
            bw.write("    </resultMap>");
            bw.newLine();
            bw.newLine();

            // 通用查询结果列
            bw.write("    <!--通用查询结果列-->");
            bw.newLine();
            bw.write("    <sql id=\"" + BASE_COLUMN_LIST + "\">");
            bw.newLine();
            StringBuilder columnBuilder = new StringBuilder();
            for (FieldInfo fieldInfo : tableinfo.getFieldList()) {
                columnBuilder.append(fieldInfo.getFieldName()).append(",");
            }
            columnBuilder.delete(columnBuilder.length() - 1, columnBuilder.length());
            bw.write("        " + columnBuilder);
            bw.newLine();
            bw.write("    </sql>");
            bw.newLine();
            bw.newLine();

            // 基础查询条件
            bw.write("    <!--基础查询条件-->");
            bw.newLine();
            bw.write("    <sql id=\"" + BASE_QUERY_CONDITION + "\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableinfo.getFieldList()) {
                String stringQuery = "";
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    stringQuery = " and query." + fieldInfo.getPropertyName() + " != ''";
                }
                bw.write("        <if test=\"query." + fieldInfo.getPropertyName() + " != null" + stringQuery + "\">");
                bw.newLine();
                bw.write(
                        "            and " + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "}");
                bw.newLine();
                bw.write("        </if>");
                bw.newLine();
            }
            bw.write("    </sql>");
            bw.newLine();

            // 扩展查询条件
            bw.newLine();
            bw.write("    <!--扩展查询条件-->");
            bw.newLine();
            bw.write("    <sql id=\"" + BASE_QUERY_CONDITION_EXTEND + "\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableinfo.getExtendFieldList()) {
                String andWhere = "";
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    andWhere =
                            " and " + fieldInfo.getFieldName() + " like concat('%', #{query." + fieldInfo.getPropertyName() + "}, '%')";
                } else if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(
                        Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                    if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_START)) {
                        andWhere =
                                "<![CDATA[ and " + fieldInfo.getFieldName() + " >= str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d') ]]>";
                    } else if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_END)) {
                        andWhere =
                                "<![CDATA[ and " + fieldInfo.getFieldName() + " < date_sub(str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d'), interval -1 day) ]]>";
                    }

                }
                bw.write(
                        "        <if test=\"query." + fieldInfo.getPropertyName() + " != null and query." + fieldInfo.getPropertyName() + " != ''\">");
                bw.newLine();
                bw.write("            " + andWhere);
                bw.newLine();
                bw.write("        </if>");
                bw.newLine();
            }
            bw.write("    </sql>");
            bw.newLine();

            // 通用查询条件
            bw.newLine();
            bw.write("    <!--通用查询条件-->");
            bw.newLine();
            bw.write("    <sql id=\"" + QUERY_CONDITION + "\">");
            bw.newLine();
            bw.write("        <where>");
            bw.newLine();
            bw.write("            <include refid=\"" + BASE_QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("            <include refid=\"" + BASE_QUERY_CONDITION_EXTEND + "\"/>");
            bw.newLine();
            bw.write("        </where>");
            bw.newLine();
            bw.write("    </sql>");
            bw.newLine();

            // 查询列表
            bw.newLine();
            bw.write("    <!--查询列表-->");
            bw.newLine();
            bw.write("    <select id=\"selectList\" resultMap=\"base_result_map\">");
            bw.newLine();
            bw.write(
                    "        SELECT\n" + "        <include refid=\"" + BASE_COLUMN_LIST + "\"/>\n" + "        FROM " + tableinfo.getTableName() + "\n" + "        <include refid=\"" + QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("        <if test=\"query.orderBy!=null\">\n" +
                "            order by ${query.orderBy}\n" +
                "        </if>");
            bw.newLine();
            bw.write("        <if test=\"query.simplePage!=null\">\n" +
                    "            limit #{query.simplePage.start},#{query.simplePage.end}\n" +
                    "        </if>");
            bw.newLine();
            bw.write("    </select>");
            bw.newLine();

            // 查询数量
            bw.newLine();
            bw.write("    <!--查询数量-->");
            bw.newLine();
            bw.write("    <select id=\"selectCount\" resultType=\"java.lang.Integer\">");
            bw.newLine();
            bw.write("        SELECT COUNT(1) FROM " + tableinfo.getTableName());
            bw.newLine();
            bw.write("        <include refid=\"" + QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("    </select>");
            bw.newLine();

            // 单条插入
            bw.newLine();
            bw.write("    <!--插入（匹配有值的字段）-->");
            bw.newLine();
            bw.write("    <insert id=\"insert\" parameterType=\"" + poClass + "\">");
            bw.newLine();
            // 拿到自增长 id
            FieldInfo autoIncrementField = null;
            for (FieldInfo fieldInfo : tableinfo.getFieldList()) {
                if (fieldInfo.getAutoIncrement()) {
                    autoIncrementField = fieldInfo;
                    // 自增长只有一个，拿到即可退出
                    break;
                }
            }
            if (autoIncrementField != null) {
                bw.write("        <selectKey keyProperty=\"bean." + autoIncrementField.getFieldName() + "\" resultType=\"" + autoIncrementField.getJavaType() + "\" order=\"AFTER\">\n" +
                        "            SELECT LAST_INSERT_ID()\n" +
                        "        </selectKey>");
                bw.newLine();
            }
            bw.write("        INSERT INTO " + tableinfo.getTableName());
            bw.newLine();
            bw.write("        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableinfo.getFieldList()) {
                if (fieldInfo.getAutoIncrement()) {
                    continue;
                }
                bw.write("            <if test=\"bean." + fieldInfo.getPropertyName() + " != null\">\n" +
                        "                " + fieldInfo.getFieldName() + ",\n" +
                        "            </if>");
                bw.newLine();
            }
            bw.write("        </trim>");
            bw.newLine();
            bw.write("        <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableinfo.getFieldList()) {
                if (fieldInfo.getAutoIncrement()) {
                    continue;
                }
                bw.write("            <if test=\"bean." + fieldInfo.getPropertyName() + " != null\">\n" +
                        "                #{bean." + fieldInfo.getPropertyName() + "},\n" +
                        "            </if>");
                bw.newLine();
            }
            bw.write("        </trim>");
            bw.newLine();
            bw.write("    </insert>");
            bw.newLine();

            // 插入或更新（匹配有值的字段）
            bw.newLine();
            bw.write("    <!--插入或更新（匹配有值的字段）-->");
            bw.newLine();
            bw.write("    <insert id=\"insertOrUpdate\" parameterType=\"" + Constants.PACKAGE_PO + "." + tableinfo.getBeanName() + "\">");
            bw.newLine();
            bw.write("        INSERT INTO " + tableinfo.getTableName());
            bw.newLine();
            bw.write("        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableinfo.getFieldList()) {
                // 若是主键则略过
                if (idFieldList.contains(fieldInfo.getPropertyName())) {
                    continue;
                }
                bw.write("            <if test=\"bean." + fieldInfo.getPropertyName() + " != null\">\n" +
                        "                " + fieldInfo.getFieldName() + ",\n" +
                        "            </if>");
                bw.newLine();
            }
            bw.write("        </trim>");
            bw.newLine();
            bw.write("        <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableinfo.getFieldList()) {
                // 若是主键则略过
                if (idFieldList.contains(fieldInfo.getPropertyName())) {
                    continue;
                }
                bw.write("            <if test=\"bean." + fieldInfo.getPropertyName() + " != null\">\n" +
                        "                #{bean." + fieldInfo.getPropertyName() + "},\n" +
                        "            </if>");
                bw.newLine();
            }
            bw.write("        </trim>");
            bw.newLine();
            // 此后为 update
            bw.write("        on DUPLICATE key update");
            bw.newLine();
            bw.write("        <trim prefix=\"\" suffix=\"\" suffixOverrides=\",\">");
            bw.newLine();
            // logger.info(keyIndexMap.toString());
            // 将所有索引的字段值取出，此处为解决的问题，需要定义一个新 map 获得所有索引
            Map<String, String> keyTempMap = new HashMap<>();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                for (FieldInfo fieldInfo : fieldInfoList) {
                    keyTempMap.put(fieldInfo.getFieldName(), fieldInfo.getFieldName());
                }
            }
            for (FieldInfo fieldInfo : tableinfo.getFieldList()) {
                // 实际生产中是唯一索引和主键都是不能动的，所以均需要略过，操作的时候也是若是遇上这几个已存在的则向后加记录，否则修改记录
                // 若是唯一索引则略过
                if (keyTempMap.containsKey(fieldInfo.getFieldName())) {
                    // logger.info(fieldInfo.getFieldName());
                    continue;
                }
                bw.write("            <if test=\"bean." + fieldInfo.getPropertyName() + " != null\">\n" +
                        "                " + fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() + "),\n" +
                        "            </if>");
                bw.newLine();
            }
            bw.write("        </trim>");
            bw.newLine();
            bw.write("    </insert>");
            bw.newLine();

            // 添加（批量插入）
            bw.newLine();
            bw.write("    <!--添加（批量插入）-->");
            bw.newLine();
            bw.write("    <insert id=\"insertBatch\" parameterType=\"" + poClass + "\">");
            bw.newLine();
            StringBuffer insertFieldBuffer = new StringBuffer();
            StringBuffer insertPropertyBuffer = new StringBuffer();
            StringBuffer updatePropertyBuffer = new StringBuffer();
            for (FieldInfo fieldInfo : tableinfo.getFieldList()) {
                // 略过自增长
                if (fieldInfo.getAutoIncrement()) {
                    continue;
                }
                insertFieldBuffer.append(fieldInfo.getFieldName()).append(",");
                insertPropertyBuffer.append("#{item.").append(fieldInfo.getPropertyName()).append("}").append(",");
                updatePropertyBuffer.append("        ").append(fieldInfo.getFieldName()).append(" = VALUES(" + fieldInfo.getFieldName() + "),\n");
            }
            insertFieldBuffer.deleteCharAt(insertFieldBuffer.length() - 1);
            insertPropertyBuffer.deleteCharAt(insertPropertyBuffer.length() - 1);
            updatePropertyBuffer.deleteCharAt(updatePropertyBuffer.length() - 1);
            // 多一个逗号
            updatePropertyBuffer.deleteCharAt(updatePropertyBuffer.length() - 1);

            bw.write("        INSERT INTO " + tableinfo.getTableName() + "(" + insertFieldBuffer + ") VALUES ");
            bw.newLine();
            // 此处有问题，因为是批量插入，所以不该 open="(" close=")" 而应该在每组数据都加一对括号
            bw.write("        <foreach collection=\"list\" item=\"item\" separator=\",\" >");
            bw.newLine();
            bw.write("            (" + insertPropertyBuffer + ")");
            bw.newLine();
            bw.write("        </foreach>");
            bw.newLine();
            bw.write("    </insert>");
            bw.newLine();

            // 批量新增修改（批量插入）
            bw.newLine();
            bw.write("    <!--批量新增修改（批量插入）-->");
            bw.newLine();
            bw.write("    <insert id=\"insertOrUpdateBatch\" parameterType=\"" + poClass + "\">");
            bw.newLine();
            // StringBuffer insertFieldBuffer = new StringBuffer();
            // StringBuffer insertPropertyBuffer = new StringBuffer();
            // StringBuffer updatePropertyBuffer = new StringBuffer();
            // for (FieldInfo fieldInfo : tableinfo.getFieldList()) {
            //     // 略过自增长
            //     if (fieldInfo.getAutoIncrement()) {
            //         continue;
            //     }
            //     insertFieldBuffer.append(fieldInfo.getFieldName()).append(",");
            //     insertPropertyBuffer.append("#(item.").append(fieldInfo.getFieldName()).append(")").append(",");
            //     updatePropertyBuffer.append("        ").append(fieldInfo.getFieldName()).append(" = VALUES(" + fieldInfo.getFieldName() + "),\n");
            // }
            // insertFieldBuffer.deleteCharAt(insertFieldBuffer.length() - 1);
            // insertPropertyBuffer.deleteCharAt(insertPropertyBuffer.length() - 1);
            // updatePropertyBuffer.deleteCharAt(updatePropertyBuffer.length() - 1);
            bw.write("        INSERT INTO " + tableinfo.getTableName() + "(" + insertFieldBuffer + ") VALUES ");
            bw.newLine();
            bw.write("        <foreach collection=\"list\" item=\"item\" separator=\",\" >");
            bw.newLine();
            bw.write("            (" + insertPropertyBuffer + ")");
            bw.newLine();
            bw.write("        </foreach>");
            bw.newLine();
            bw.write("        on DUPLICATE key update");
            bw.newLine();
            bw.write(updatePropertyBuffer.toString());
            bw.newLine();
            bw.write("    </insert>");
            bw.newLine();

            // 根据索引修改
            bw.newLine();
            bw.write("    <!--根据索引的一些操作-->");
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> keyFieldInfoList = entry.getValue();

                Integer index = 0;
                // 方法名
                StringBuilder methodName = new StringBuilder();
                StringBuffer paramNameBuffer = new StringBuffer();

                for (FieldInfo fieldInfo : keyFieldInfoList) {
                    index++;
                    methodName.append(StringUtils.upperCaseFirsrLetter(fieldInfo.getPropertyName()));
                    paramNameBuffer.append(fieldInfo.getFieldName() + " = #{" + fieldInfo.getPropertyName() + "}");
                    if (index < keyFieldInfoList.size()) {
                        methodName.append("And");
                        paramNameBuffer.append(" and ");
                    }
                }
                bw.newLine();

                // 生成注释
                bw.write("    <!--根据 " + methodName + " 查询-->");
                bw.newLine();
                // 生成查询
                bw.write("    <select id=\"selectBy" + methodName + "\" resultMap=\"base_result_map\">");
                bw.newLine();

                bw.write("        select\n" +
                        "        <include refid=\"base_column_list\"/>\n" +
                        "        from " + tableinfo.getTableName() + " where " + paramNameBuffer);
                bw.newLine();
                bw.write("    </select>");
                bw.newLine();

                // 生成注释
                bw.newLine();
                bw.write("    <!--根据 " + methodName + " 更新-->");
                bw.newLine();
                // 生成更新
                bw.write("    <update id=\"updateBy" + methodName + "\" parameterType=\"" + poClass + "\">");
                bw.newLine();
                bw.write("        update " + tableinfo.getTableName());
                bw.newLine();
                bw.write("        <set>");
                bw.newLine();
                for (FieldInfo fieldInfo : tableinfo.getFieldList()) {
                    bw.write("            <if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                    bw.newLine();
                    // 测试的时候发现少了 bean.
                    bw.write("                " + fieldInfo.getFieldName() + " = #{bean." + fieldInfo.getPropertyName() + "},");
                    bw.newLine();
                    bw.write("            </if>");
                    bw.newLine();
                }
                bw.write("        </set>");
                bw.newLine();
                bw.write("        where " + paramNameBuffer);
                bw.newLine();
                bw.write("    </update>");
                bw.newLine();
                bw.newLine();

                // 生成注释
                bw.newLine();
                bw.write("    <!--根据 " + methodName + " 删除-->");
                bw.newLine();
                // 生成删除
                bw.write("    <delete id=\"deleteBy" + methodName + "\">");
                bw.newLine();
                bw.write("        delete from " + tableinfo.getTableName() + " where " + paramNameBuffer);
                bw.newLine();
                bw.write("    </delete>");
                bw.newLine();
                bw.newLine();
            }

            bw.write("</mapper>");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建 mappers xml 失败", e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
