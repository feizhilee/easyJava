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

public class BuildController {
    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_CONTROLLER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String className = tableInfo.getBeanName() + "Controller";
        File poFile = new File(folder, className + ".java");

        OutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            osw = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(osw);
            bw.write("package " + Constants.PACKAGE_CONTROLLER + ";");
            bw.newLine();
            bw.newLine();

            String serviceName = tableInfo.getBeanName() + Constants.SUFFIX_SERIVCE;
            String serviceBeanName = StringUtils.lowererCaseFirsrLetter(serviceName);

            // 导入包
            // bw.write("import java.io.Serializable;");
            // bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_SERVICE + "." + serviceName + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".ResponseVO;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestBody;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestMapping;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RestController;");
            bw.newLine();
            bw.newLine();
            bw.write("import javax.annotation.Resource;");
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
            BuildComment.createClassComment(bw, tableInfo.getComment() + "Controller");
            bw.write("@RestController(\"/" + StringUtils.lowererCaseFirsrLetter(tableInfo.getBeanName()) + "Controller\")");
            bw.newLine();
            bw.write("@RequestMapping(\"/" + StringUtils.lowererCaseFirsrLetter(tableInfo.getBeanName()) + "\")");
            bw.newLine();
            bw.write("public class " + className + " extends ABaseController {");
            bw.newLine();
            bw.write("    @Resource");
            bw.newLine();
            bw.write("    private " + serviceName + " " + serviceBeanName + ";");
            bw.newLine();
            bw.newLine();

            // 根据条件分页查询
            BuildComment.createFieldComment(bw, "根据条件分页查询");
            bw.write("\t@RequestMapping(\"/loadDataList\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO loadDataList(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".findListByPage(query));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            // 新增
            BuildComment.createFieldComment(bw, "新增");
            bw.write("\t@RequestMapping(\"/add\")");
            bw.newLine();
            bw.write("    public ResponseVO add(" + tableInfo.getBeanName() + " bean) {");
            bw.newLine();
            bw.write("\t\tthis." + serviceBeanName + ".add(bean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("    }");
            bw.newLine();
            bw.newLine();

            // 批量新增
            BuildComment.createFieldComment(bw, "批量新增");
            bw.write("\t@RequestMapping(\"/addBatch\")");
            bw.newLine();
            bw.write("    public ResponseVO addBatch (@RequestBody List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tthis." + serviceBeanName + ".addBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("    }");
            bw.newLine();
            bw.newLine();

            // 批量新增或修改
            BuildComment.createFieldComment(bw, "批量新增或修改");
            bw.write("\t@RequestMapping(\"/addOrUpdateBatch\")");
            bw.newLine();
            bw.write("    public ResponseVO addOrUpdateBatch (@RequestBody List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tthis." + serviceBeanName + ".addOrUpdateBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("    }");
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
                // 方法体传掺
                StringBuilder methodBodyParams = new StringBuilder();
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

                    methodBodyParams.append(fieldInfo.getPropertyName());
                    if (index < keyFieldInfoList.size()) {
                        methodBodyParams.append(", ");
                    }
                }
                // 生成注释
                BuildComment.createFieldComment(bw, "根据 " + methodName + " 查询");
                bw.write("\t@RequestMapping(\"/get" + tableInfo.getBeanName() + "By" + methodName + "\")");
                bw.newLine();
                // 生成查询
                bw.write("\tpublic ResponseVO get" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".get" + tableInfo.getBeanName() + "By" + methodName + "(" + methodBodyParams + ")) ;");
                bw.newLine();
                bw.write("    }");
                bw.newLine();
                bw.newLine();

                // 生成注释
                BuildComment.createFieldComment(bw, "根据 " + methodName + " 更新");
                bw.write("\t@RequestMapping(\"/update" + tableInfo.getBeanName() + "By" + methodName + "\")");
                bw.newLine();
                // 生成更新
                bw.write("\tpublic ResponseVO update" + tableInfo.getBeanName() + "By" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + ") {");
                bw.newLine();
                bw.write("\t\tthis." + serviceBeanName + ".update" + tableInfo.getBeanName() + "By" + methodName + "(bean, " + methodBodyParams + ");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("    }");
                bw.newLine();
                bw.newLine();

                // 生成注释
                BuildComment.createFieldComment(bw, "根据 " + methodName + " 删除");
                bw.write("\t@RequestMapping(\"/delete" + tableInfo.getBeanName() + "By" + methodName + "\")");
                bw.newLine();
                // 生成删除
                bw.write("\tpublic ResponseVO delete" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\tthis." + serviceBeanName + ".delete" + tableInfo.getBeanName() + "By" + methodName + "(" + methodBodyParams + ");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("    }");
                bw.newLine();
                bw.newLine();
            }

            bw.write("}");

            bw.flush();
        } catch (Exception e) {
            logger.error("创建 service impl 失败", e);
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
