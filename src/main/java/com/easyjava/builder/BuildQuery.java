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
import java.util.List;

public class BuildQuery {
    private static final Logger logger = LoggerFactory.getLogger(BuildQuery.class);

    public static void execute(TableInfo tableinfo) {
        File folder = new File(Constants.PATH_QUERY);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String className = tableinfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY;

        File poFile = new File(folder, className + ".java");

        OutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            osw = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(osw);
            bw.write("package " + Constants.PACKAGE_QUERY + ";");
            bw.newLine();
            bw.newLine();

            // 导入包
            if (tableinfo.getHaveDate() || tableinfo.getHaveDateTime()) {
                bw.write("import java.util.Date;");
                bw.newLine();
            }

            if (tableinfo.getHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
            }
            bw.newLine();
            bw.newLine();

            // 构建类注释
            BuildComment.createClassComment(bw, tableinfo.getComment() + "查询对象");
            bw.write("public class " + className + " extends BaseQuery {");
            bw.newLine();
            bw.newLine();

            // 用来存 Fuzzy、TimeStart、TimeEnd、DateStart、DateEnd，用于后续生成 getter 和 setter，改为在 BuildTable 中生成了
            for (FieldInfo field : tableinfo.getFieldList()) {
                BuildComment.createFieldComment(bw, field.getComment());

                // 添加注解
                bw.write("\tprivate " + field.getJavaType() + " " + field.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();

                // String 类型的参数
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, field.getSqlType())) {
                    String propertyName = field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY;
                    bw.write("\tprivate " + field.getJavaType() + " " + propertyName + ";");
                    bw.newLine();
                    bw.newLine();
                }

                // 日期类型
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, field.getSqlType()) || ArrayUtils.contains(
                    Constants.SQL_DATE_TIME_TYPES, field.getSqlType())) {
                    bw.write(
                        "\tprivate String " + field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START + ";");
                    bw.newLine();
                    bw.newLine();

                    bw.write(
                        "\tprivate String " + field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END + ";");
                    bw.newLine();
                    bw.newLine();
                }
            }

            // fieldInfoList.addAll(extendList);

            // 构造 getter 和 setter 方法
            buildGetSet(tableinfo.getFieldList(), bw);
            buildGetSet(tableinfo.getExtendFieldList(), bw);
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建 po 失败", e);
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

    // 构造 getter 和 setter 方法，如果不提取出来，在构造 mapper.xml 的时候会多很多 fuzzy
    private static void buildGetSet(List<FieldInfo> fieldInfoList, BufferedWriter bw) throws IOException {
        for (FieldInfo field : fieldInfoList) {
            // setter
            String tempField = StringUtils.upperCaseFirsrLetter(field.getPropertyName());
            bw.write(
                "\tpublic void set" + tempField + "(" + field.getJavaType() + " " + field.getPropertyName() + ") {");
            bw.newLine();
            bw.write("\t\tthis." + field.getPropertyName() + " = " + field.getPropertyName() + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            // getter
            bw.write("\tpublic " + field.getJavaType() + " get" + tempField + "() {");
            bw.newLine();
            bw.write("\t\treturn this." + field.getPropertyName() + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
        }
    }
}
