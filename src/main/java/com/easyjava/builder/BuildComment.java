package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.utils.DateUtils;

import java.io.BufferedWriter;
import java.util.Date;

public class BuildComment {
    public static void createClassComment(BufferedWriter bw, String classComment) throws Exception {
        /**
         *
         * @Description:
         * @Author: YoshuaLee
         * @Date: 2025/3/4
         *
         */
        bw.write("/**");
        bw.newLine();
        bw.write(" * @Description: " + classComment);
        bw.newLine();
        bw.write(" * @Author: " + Constants.CODE_AUTHOR);
        bw.newLine();
        bw.write(" * @Date: " + DateUtils.format(new Date(), DateUtils._YYYYMMDD));
        bw.newLine();
        bw.write(" */");
        bw.newLine();
    }

    public static void createFieldComment(BufferedWriter bw, String fieldComment) throws Exception {
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * " + fieldComment);
        bw.newLine();
        bw.write("\t */");
        bw.newLine();
    }

    public static void createMethodComment(BufferedWriter bw) {
    }

}
