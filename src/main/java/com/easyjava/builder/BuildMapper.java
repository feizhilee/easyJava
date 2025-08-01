package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

public class BuildMapper {
    private static final Logger logger = LoggerFactory.getLogger(BuildMapper.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_MAPPERS);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;
        File poFile = new File(folder, className + ".java");

        OutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            osw = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(osw);
            bw.write("package " + Constants.PACKAGE_MAPPERS + ";");
            bw.newLine();
            bw.newLine();

            // 导入包
            bw.write("import org.apache.ibatis.annotations.Param;");
            bw.newLine();
            bw.newLine();

            // 构建类注释
            BuildComment.createClassComment(bw, tableInfo.getComment() + " Mapper");
            bw.write("public interface " + className + "<T, P> extends BaseMapper {");
            bw.newLine();
            bw.newLine();

            // 遍历索引
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> keyFieldInfoList = entry.getValue();

                Integer index = 0;
                // 方法名
                StringBuilder methodName = new StringBuilder();
                // 方法参数
                StringBuilder methodParams = new StringBuilder();
                for (FieldInfo fieldInfo : keyFieldInfoList) {
                    index++;
                    methodName.append(StringUtils.upperCaseFirsrLetter(fieldInfo.getPropertyName()));
                    if (index < keyFieldInfoList.size()) {
                        methodName.append("And");
                    }

                    methodParams.append(
                        "@Param(\"" + fieldInfo.getPropertyName() + "\") " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                    if (index < keyFieldInfoList.size()) {
                        methodParams.append(", ");
                    }
                }
                // 生成注释
                BuildComment.createFieldComment(bw, "根据 " + methodName + " 查询");
                // 生成查询
                bw.write("\tT selectBy" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();

                // 生成注释
                BuildComment.createFieldComment(bw, "根据 " + methodName + " 更新");
                // 生成更新
                bw.write("\tInteger updateBy" + methodName + "(@Param(\"bean\") T t, " + methodParams + ");");
                bw.newLine();
                bw.newLine();

                // 生成注释
                BuildComment.createFieldComment(bw, "根据 " + methodName + " 删除");

                // 生成删除
                bw.write("\tInteger deleteBy" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();
            }
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建 mappers 失败", e);
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
