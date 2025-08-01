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

public class BuildService {
    private static final Logger logger = LoggerFactory.getLogger(BuildService.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_SERVICE);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String className = tableInfo.getBeanName() + "Service";
        File poFile = new File(folder, className + ".java");

        OutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            osw = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(osw);
            bw.write("package " + Constants.PACKAGE_SERVICE + ";");
            bw.newLine();
            bw.newLine();

            // 导入包
            // bw.write("import java.io.Serializable;");
            // bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".PaginationResultVO;");
            bw.newLine();
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();


            // if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
            //     bw.write("import java.util.Date;");
            //     bw.newLine();
            //     bw.write(Constants.BEAN_DATE_FORMAT_CLASS);
            //     bw.newLine();
            //     bw.write(Constants.BEAN_DATE_UNFORMAT_CLASS);
            //     bw.newLine();
            //     bw.write("import " + Constants.PACKAGE_ENUMS + ".DateTimePatternEnum;");
            //     bw.newLine();
            //     bw.write("import " + Constants.PACKAGE_UTILS + ".DateUtils;");
            //     bw.newLine();
            // }

            bw.newLine();
            BuildComment.createClassComment(bw, tableInfo.getComment() + "Service");
            bw.write("public interface " + className + " {");
            bw.newLine();

            // 根据条件查询列表
            BuildComment.createFieldComment(bw, "根据条件查询列表");
            bw.write("    List<" + tableInfo.getBeanName() + "> findListByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            // 根据条件查询数量
            BuildComment.createFieldComment(bw, "根据条件查询数量");
            bw.write("    Integer findCountByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            // 分页查询
            BuildComment.createFieldComment(bw, "分页查询");
            bw.write("    PaginationResultVO<" + tableInfo.getBeanName() + "> findListByPage(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            // 新增
            BuildComment.createFieldComment(bw, "新增");
            bw.write("    Integer add(" + tableInfo.getBeanName() + " bean);");
            bw.newLine();
            bw.newLine();

            // 批量新增
            BuildComment.createFieldComment(bw, "批量新增");
            bw.write("    Integer addBatch (List<" + tableInfo.getBeanName() + "> listBean);");
            bw.newLine();
            bw.newLine();

            // 批量新增或修改
            BuildComment.createFieldComment(bw, "批量新增或修改");
            bw.write("    Integer addOrUpdateBatch (List<" + tableInfo.getBeanName() + "> listBean);");
            bw.newLine();
            bw.newLine();

            // 插入更新删除
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

                    methodParams.append(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                    if (index < keyFieldInfoList.size()) {
                        methodParams.append(", ");
                    }
                }
                // 生成注释
                BuildComment.createFieldComment(bw, "根据 " + methodName + " 查询");
                // 生成查询
                bw.write("\t" + tableInfo.getBeanName() + " get" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();

                // 生成注释
                BuildComment.createFieldComment(bw, "根据 " + methodName + " 更新");
                // 生成更新
                bw.write("\tInteger update" + tableInfo.getBeanName() + "By" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + ");");
                bw.newLine();
                bw.newLine();

                // 生成注释
                BuildComment.createFieldComment(bw, "根据 " + methodName + " 删除");
                // 生成查询
                bw.write("\tInteger delete" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();
            }

            bw.write("}");

            bw.flush();
        } catch (Exception e) {
            logger.error("创建 service 失败", e);
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
