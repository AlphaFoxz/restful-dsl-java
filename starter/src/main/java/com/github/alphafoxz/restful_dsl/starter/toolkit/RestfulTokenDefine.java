package com.github.alphafoxz.restful_dsl.starter.toolkit;

public interface RestfulTokenDefine {
    public static final String COMMENT_LINE = "comment_line";
    public static final String COMMENT_BLOCK = "comment_block";
    public static final String COMMENT = "COMMENT";
    /**
     * 命名空间定义
     */
    public static final String NAMESPACE_LANG = "namespace_lang";
    public static final String NAMESPACE_VALUE = "namespace_value";
    public static final String NAMESPACE = "namespace";
    /**
     * import引用
     */
    public static final String IMPORT_VALUE = "import_value";
    public static final String IMPORT = "import";
    /**
     * 预置类型定义（built-in type）
     */
    public static final String CONTAIN_MAP_KEYTYPE = "contain_map_keytype";
    public static final String CONTAIN_MAP_VALUETYPE = "contain_map_valuetype";
    public static final String CONTAIN_MAP = "contain_map";
    public static final String CONTAIN_LIST_TYPE = "contain_list_type";
    public static final String CONTAIN_LIST = "contain_list";
    public static final String CONTAIN_SET_TYPE = "contain_set_type";
    public static final String CONTAIN_SET = "contain_set";
    public static final String REF_ENUM = "ref_enum";
    public static final String INTYPE = "intype";

    public static interface Intypes {
        public static final String BOOLEAN = "boolean";
        public static final String BYTE = "byte";
        public static final String I16 = "i16";
        public static final String SHORT = "short";
        public static final String I32 = "i32";
        public static final String INT = "int";
        public static final String I64 = "i64";
        public static final String LONG = "long";
        public static final String DOUBLE = "double";
        public static final String BINARY = "binary";
        public static final String STRING = "string";
    }

    /**
     * 用户类型定义
     */
    public static final String UTYPE_NAMESPACE = "utype_namespace";
    public static final String UTYPE_CUSTOMNAME = "utype_customname";
    public static final String UTYPE = "utype";
    public static final String TYPE = "type";
    public static final String VOID = "void";
    /**
     * 类型修饰符定义
     */
    public static final String REQUIRED = "required";
    /**
     * class定义
     */
    public static final String CLASS_NAME = "class_name";
    public static final String CLASS_FIELD_NAME = "class_field_name";
    public static final String CLASS_FIELD = "class_field";
    public static final String CLASS = "class";
    /**
     * 枚举定义
     */
    public static final String ENUM_NAME = "enum_name";
    public static final String ENUM_INSTANCE_NAME = "enum_instance_name";
    public static final String ENUM_INSTANCE_CONSTANT = "enum_instance_constant";
    public static final String ENUM_INSTANCE = "enum_instance";
    public static final String ENUM = "enum";
    /**
     * 入参定义
     */
    public static final String PARAM_NAME = "param_name";
    public static final String PARAM = "param";
    /**
     * interface定义
     */
    public static final String INTERFACE_NAME = "interface_name";
    public static final String INTERFACE_FUNCTION_NAME = "interface_function_name";
    public static final String INTERFACE_FUNCTION = "interface_function";
    public static final String INTERFACE = "interface";
    /**
     * 注解定义
     */
    public static final String ANNOTATION_NAME = "annotation_name";
    public static final String ANNOTATION_VALUE = "annotation_value";
    public static final String ANNOTATION = "annotation";
    public static final String ROOT = "root";
    public static final String EOI = "EOI";
}
