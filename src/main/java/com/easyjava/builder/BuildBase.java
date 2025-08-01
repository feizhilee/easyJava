package com.easyjava.builder;

import com.easyjava.bean.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// 读取模版生成 Java 类
public class BuildBase {

    private static final Logger logger = LoggerFactory.getLogger(BuildBase.class);

    public static void execute() {
        List<String> headerInfoList = new ArrayList();

        // 生成 date 枚举
        headerInfoList.add("package " + Constants.PACKAGE_ENUMS);
        build(headerInfoList, "DateTimePatternEnum", Constants.PATH_ENUMS);

        // 生成 PageSize 枚举
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_ENUMS);
        build(headerInfoList, "PageSize", Constants.PATH_ENUMS);

        // 生成时间工具类
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_UTILS);
        build(headerInfoList, "DateUtils", Constants.PATH_UTILS);

        // 生成 BaseMapper
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_MAPPERS);
        build(headerInfoList, "BaseMapper", Constants.PATH_MAPPERS);

        // 生成分页信息
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_QUERY);
        headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".PageSize");
        build(headerInfoList, "SimplePage", Constants.PATH_QUERY);

        // 生成基础查询
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_QUERY);
        build(headerInfoList, "BaseQuery", Constants.PATH_QUERY);

        // 生成 PaginationResultVO
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_VO);
        build(headerInfoList, "PaginationResultVO", Constants.PATH_VO);

        // 生成 ResponseCodeEnum
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_ENUMS);
        build(headerInfoList, "ResponseCodeEnum", Constants.PATH_ENUMS);

        // 生成 BusinessException
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_EXCEPTION);
        headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum");
        build(headerInfoList, "BusinessException", Constants.PATH_EXCEPTION);

        // 生成 ABaseController
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER);
        headerInfoList.add("import " + Constants.PACKAGE_VO + ".ResponseVO");
        headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum");
        headerInfoList.add("import " + Constants.PACKAGE_EXCEPTION + ".BusinessException");
        build(headerInfoList, "ABaseController", Constants.PATH_CONTROLLER);

        // 生成 AGlobalExceptionHandlerController
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER);
        headerInfoList.add("import " + Constants.PACKAGE_VO + ".ResponseVO");
        headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum");
        headerInfoList.add("import " + Constants.PACKAGE_EXCEPTION + ".BusinessException");
        build(headerInfoList, "AGlobalExceptionHandlerController", Constants.PATH_CONTROLLER);

        // 生成 ResponseVO
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_VO);
        build(headerInfoList, "ResponseVO", Constants.PATH_VO);
    }

    private static void build(List<String> headerInfoList, String fileName, String outPutPath) {
        File folder = new File(outPutPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File javaFile = new File(outPutPath, fileName + ".java");

        // 读入一个文件输出到另一个文件中
        InputStream in = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        OutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        try {
            out = new FileOutputStream(javaFile);
            osw = new OutputStreamWriter(out, "UTF-8");
            bw = new BufferedWriter(osw);

            String templatePath =
                    BuildBase.class.getClassLoader().getResource("template/" + fileName + ".txt").getPath();
            in = new FileInputStream(templatePath);
            isr = new InputStreamReader(in, "UTF-8");
            br = new BufferedReader(isr);

            for (String header : headerInfoList) {
                bw.write(header + ";");
                bw.newLine();
                if (header.contains("package")) {
                    bw.newLine();
                }
            }
            String lineInfo = null;
            while ((lineInfo = br.readLine()) != null) {
                bw.write(lineInfo);
                bw.newLine();
            }
            bw.flush();

        } catch (Exception e) {
            logger.error("生成基础类: {}, 失败: ", fileName, e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
