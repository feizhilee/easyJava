package com.easyjava.bean;

import com.easyjava.utils.PropertiesUtils;

public class Constants {
    public final static String[] SQL_DATE_TIME_TYPES = new String[]{"datetime", "timestamp"};
    public final static String[] SQL_DATE_TYPES = new String[]{"date"};
    public final static String[] SQL_DECIMAL_TYPES = new String[]{"decimal", "double", "float"};
    public final static String[] SQL_INTEGER_TYPES = new String[]{"tinyint", "int"};
    public final static String[] SQL_STRING_TYPES = new String[]{"char", "varchar", "text", "longtext", "mediumtext"};
    public final static String[] SQL_LONG_TYPES = new String[]{"bigint"};

    public static Boolean IGNORE_TABLE_PREFIX;
    public static String SUFFIX_BEAN_QUERY;

    // 参数模糊搜索后缀
    public static String SUFFIX_BEAN_QUERY_FUZZY;

    // 参数日期起止
    public static String SUFFIX_BEAN_QUERY_TIME_START;
    public static String SUFFIX_BEAN_QUERY_TIME_END;

    // mappers 后缀
    public static String SUFFIX_MAPPERS;
    // service 后缀
    public static String SUFFIX_SERIVCE;

    // 需要忽略的属性
    public static String IGNORE_BEAN_TOJSON_FILED;
    public static String IGNORE_BEAN_TOJSON_EXPRESSION;
    public static String IGNORE_BEAN_TOJSON_CLASS;

    public static String CODE_AUTHOR;

    // 日期序列化、反序列化
    public static String BEAN_DATE_FORMAT_EXPRESSION;
    public static String BEAN_DATE_FORMAT_CLASS;
    public static String BEAN_DATE_UNFORMAT_EXPRESSION;
    public static String BEAN_DATE_UNFORMAT_CLASS;

    public static String PACKAGE_BASE;
    public static String PACKAGE_PO;
    public static String PACKAGE_QUERY;
    public static String PACKAGE_VO;
    public static String PACKAGE_UTILS;
    public static String PACKAGE_ENUMS;
    public static String PACKAGE_EXCEPTION;
    public static String PACKAGE_MAPPERS;
    public static String PACKAGE_SERVICE;
    public static String PACKAGE_SERVICE_IMPL;
    public static String PACKAGE_CONTROLLER;

    public static String PATH_UTILS;
    public static String PATH_JAVA = "java";
    public static String PATH_RESOURCES = "resources";
    public static String PATH_BASE;
    public static String PATH_PO;
    public static String PATH_QUERY;
    public static String PATH_VO;
    public static String PATH_ENUMS;
    public static String PATH_EXCEPTION;
    public static String PATH_MAPPERS;
    public static String PATH_MAPPERS_XMLS;
    public static String PATH_SERVICE;
    public static String PATH_SERVICE_IMPL;
    public static String PATH_CONTROLLER;

    static {
        CODE_AUTHOR = PropertiesUtils.getString("code.author");

        // 需要忽略的属性
        IGNORE_BEAN_TOJSON_FILED = PropertiesUtils.getString("ignore.bean.toJson.filed");
        IGNORE_BEAN_TOJSON_EXPRESSION = PropertiesUtils.getString("ignore.bean.toJson.expression");
        IGNORE_BEAN_TOJSON_CLASS = PropertiesUtils.getString("ignore.bean.toJson.class");
        // 日期序列化、反序列化
        BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.format.expression");
        BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getString("bean.date.format.class");
        BEAN_DATE_UNFORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.unformat.expression");
        BEAN_DATE_UNFORMAT_CLASS = PropertiesUtils.getString("bean.date.unformat.class");

        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        SUFFIX_BEAN_QUERY = PropertiesUtils.getString("suffix.bean.query");

        // 参数模糊搜索后缀
        SUFFIX_BEAN_QUERY_FUZZY = PropertiesUtils.getString("suffix.bean.query.fuzzy");

        // 参数日期起止
        SUFFIX_BEAN_QUERY_TIME_START = PropertiesUtils.getString("suffix.bean.query.time.start");
        SUFFIX_BEAN_QUERY_TIME_END = PropertiesUtils.getString("suffix.bean.query.time.end");

        // mappers 后缀
        SUFFIX_MAPPERS = PropertiesUtils.getString("suffix.mappers");
        // service 后缀
        SUFFIX_SERIVCE = PropertiesUtils.getString("suffix.service");

        PACKAGE_BASE = PropertiesUtils.getString("package.base");
        // PO
        PACKAGE_PO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.po");
        PACKAGE_QUERY = PACKAGE_BASE + "." + PropertiesUtils.getString("package.query");
        PACKAGE_VO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.vo");
        PACKAGE_UTILS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.utils");
        PACKAGE_ENUMS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.enums");
        // 异常
        PACKAGE_EXCEPTION = PACKAGE_BASE + "." + PropertiesUtils.getString("package.exception");
        PACKAGE_MAPPERS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.mappers");
        PACKAGE_SERVICE = PACKAGE_BASE + "." + PropertiesUtils.getString("package.service");
        PACKAGE_SERVICE_IMPL = PACKAGE_BASE + "." + PropertiesUtils.getString("package.service.impl");
        PACKAGE_CONTROLLER = PACKAGE_BASE + "." + PropertiesUtils.getString("package.controller");

        PATH_BASE = PropertiesUtils.getString("path.base");
        PATH_BASE = PATH_BASE + PATH_JAVA;
        PATH_PO = PATH_BASE + "/" + PACKAGE_PO.replace('.', '/');
        PATH_QUERY = PATH_BASE + "/" + PACKAGE_QUERY.replace('.', '/');
        PATH_VO = PATH_BASE + "/" + PACKAGE_VO.replace('.', '/');
        PATH_UTILS = PATH_BASE + "/" + PACKAGE_UTILS.replace('.', '/');
        // 枚举
        PATH_ENUMS = PATH_BASE + "/" + PACKAGE_ENUMS.replace('.', '/');
        // 异常
        PATH_EXCEPTION = PATH_BASE + "/" + PACKAGE_EXCEPTION.replace('.', '/');
        PATH_MAPPERS = PATH_BASE + "/" + PACKAGE_MAPPERS.replace('.', '/');
        // xml 路径
        PATH_MAPPERS_XMLS = PropertiesUtils.getString("path.base") + PATH_RESOURCES + "/" + PACKAGE_MAPPERS.replace(".", "/");
        PATH_SERVICE = PATH_BASE + "/" + PACKAGE_SERVICE.replace('.', '/');
        PATH_SERVICE_IMPL = PATH_BASE + "/" + PACKAGE_SERVICE_IMPL.replace(".", "/");
        PATH_CONTROLLER = PATH_BASE + "/" + PACKAGE_CONTROLLER.replace(".", "/");
    }

    public static void main(String[] args) {
        System.out.println(PATH_MAPPERS_XMLS);
    }

}
