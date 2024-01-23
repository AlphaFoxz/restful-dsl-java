package com.github.alphafoxz.restful_dsl.starter.toolkit;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.github.alphafoxz.restful_dsl.starter.configuration.RestfulDslProperties;
import com.github.alphafoxz.restful_dsl.starter.exception.RestfulDslException;
import com.github.alphafoxz.restful_dsl.starter.gen.restful.dtos.RestfulDslCodeTemplateDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
public final class ParseRestfulSyntaxTreeUtil implements RestfulTokenDefine {
    private static final String PAGE_CLASS_NAME = SpringUtil.getBean(RestfulDslProperties.class).getPageClass();

    public static RestfulRootBean parseRestfulRoot(RestfulDslCodeTemplateDto dto) {
        String filePath = dto.getFilePath();
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.lastIndexOf("."));

        RestfulRootBean restfulRootBean = new RestfulRootBean(dto.getImports());
        restfulRootBean.setFilePath(filePath);
        restfulRootBean.setContent(dto.getContent());
        restfulRootBean.setFileName(fileName);
        RootBean rootBean = new RootBeanBuilder(restfulRootBean, null, dto).build();
        rootBean.setName(fileName);
        restfulRootBean.setRootBean(rootBean);
        return restfulRootBean;
    }

    @Getter
    @Setter
    public static class RestfulIncludeBean implements RestfulRootIface {
        private String filePath;
        private String fileName;
        private String content;
        private final RootBean rootBean;
        private final RestfulRootBean parentRootBean;

        public RestfulIncludeBean(RestfulRootBean parentRootBean, RestfulDslCodeTemplateDto dto) {
            filePath = dto.getFilePath();
            fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.lastIndexOf("."));
            content = dto.getContent();
            rootBean = new RootBeanBuilder(parentRootBean, this, dto).build();
            this.parentRootBean = parentRootBean;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof RestfulIncludeBean) {
                RestfulIncludeBean includeBean = (RestfulIncludeBean) o;
                return StrUtil.equals(this.filePath, includeBean.getFilePath());
            }
            return false;
        }
    }

    @Data
    public static class RestfulRootBean implements RestfulRootIface {
        private String filePath;
        private String fileName;
        private String content;
        private final Set<RestfulIncludeBean> includeBeanSet = CollUtil.newHashSet();
        private final Map<String, RestfulDslCodeTemplateDto> includeDtoMap;
        private RootBean rootBean;

        public RestfulRootBean(Map<String, RestfulDslCodeTemplateDto> includeDtoMap) {
            this.includeDtoMap = includeDtoMap;
        }

        public RestfulIncludeBean getRestfulIncludeBeanByFilePath(String filePath) {
            for (RestfulIncludeBean includeBean : includeBeanSet) {
                if (StrUtil.equals(includeBean.getFilePath(), filePath)) {
                    return includeBean;
                }
            }
            return null;
        }

        public RestfulIncludeBean getRestfulIncludeBeanByFileName(String fileName) {
            for (RestfulIncludeBean includeBean : includeBeanSet) {
                if (StrUtil.equals(includeBean.getFileName(), fileName)) {
                    return includeBean;
                }
            }
            return null;
        }
    }

    @Data
    public static class RootBean {
        private String name;
        private final List<CommentBean> commentList = CollUtil.newArrayList();
        private final List<IncludeBean> includeList = CollUtil.newArrayList();
        private final Map<NamespaceBean.NamespaceLangEnum, String> namespaceMap = MapUtil.newHashMap();
        private final List<InterfaceBean> interfaceList = CollUtil.newArrayList();
        private final List<EnumBean> enumList = CollUtil.newArrayList();
        private final List<ClassBean> classList = CollUtil.newArrayList();

        public void addComment(CommentBean commentBean) {
            commentList.add(commentBean);
        }

        public void addImport(IncludeBean includeBean) {
            includeList.add(includeBean);
        }

        public void addNamespace(NamespaceBean namespaceBean) {
            namespaceMap.put(namespaceBean.namespaceLang, namespaceBean.namespaceValue);
        }

        public void addInterface(InterfaceBean interfaceBean) {
            interfaceList.add(interfaceBean);
        }

        public void addEnum(EnumBean enumBean) {
            enumList.add(enumBean);
        }

        public void addClass(ClassBean classBean) {
            classList.add(classBean);
        }

        public String getJavaNameSpace() {
            return namespaceMap.get(NamespaceBean.NamespaceLangEnum.JAVA);
        }

        public String getTsNameSpace() {
            return namespaceMap.get(NamespaceBean.NamespaceLangEnum.TS);
        }

        public String getRustNameSpace() {
            return namespaceMap.get(NamespaceBean.NamespaceLangEnum.RS);
        }
    }

    @Data
    public static class NamespaceBean {
        private NamespaceLangEnum namespaceLang;
        private String namespaceValue;

        public static enum NamespaceLangEnum {
            C_GLIB,
            CL,
            DART,
            DELPHI,
            D,
            ERL,
            GO,
            GV,
            HAXE,
            HTML,
            JAVAME,
            JAVA,
            JSON,
            JS,
            KOTLIN,
            LUA,
            MARKDOWN,
            NETSTD,
            OCAML,
            PERL,
            PHP,
            PY,
            RB,
            RS,
            ST,
            SWIFT,
            TS,
        }

        public static NamespaceLangEnum getEnumByName(String namespaceLang) {
            for (NamespaceLangEnum value : NamespaceLangEnum.values()) {
                if (value.name().equalsIgnoreCase(StrUtil.trim(namespaceLang))) {
                    return value;
                }
            }
            return null;
        }
    }

    @Data
    public static class IncludeBean {
        private String includeValue;
    }

    @Data
    public static class ClassBean {
        private CommentBean doc;
        private final List<CommentBean> commentList = CollUtil.newArrayList();
        private final Map<String, List<String>> annotationMap = MapUtil.newHashMap();
        private String className;
        private List<ClassFieldBean> classFieldList = CollUtil.newArrayList();
        private final Set<String> importTypeName = CollUtil.newHashSet();

        public void addClassField(ClassFieldBean classField) {
            importTypeName.addAll(classField.getImportTypeName());
            this.classFieldList.add(classField);
        }

        @Data
        public static class ClassFieldBean {
            private CommentBean doc;
            private final List<CommentBean> commentList = CollUtil.newArrayList();
            private Modifier modifier;
            private TypeBean type;
            private String fieldName;
            private final Set<String> importTypeName = CollUtil.newHashSet();

            public void setType(TypeBean type) {
                importTypeName.addAll(type.getImportJavaTypeName());
                this.type = type;
            }
        }
    }

    @Data
    public static class InterfaceBean {
        private CommentBean doc;
        private final List<CommentBean> commentList = CollUtil.newArrayList();
        private final Map<String, List<String>> annotationMap = MapUtil.newHashMap();
        private String interfaceName;
        private final List<InterfaceFunctionBean> interfaceFunctionList = CollUtil.newArrayList();
        private final Set<String> importTypeName = CollUtil.newHashSet();

        public void addInterfaceFunction(InterfaceFunctionBean interfaceFunction) {
            importTypeName.addAll(interfaceFunction.getImportTypeName());
            this.interfaceFunctionList.add(interfaceFunction);
        }

        @Data
        public static class InterfaceFunctionBean {
            private CommentBean doc;
            private final List<CommentBean> commentList = CollUtil.newArrayList();
            private final Map<String, List<String>> annotationMap = MapUtil.newHashMap();
            private TypeBean returnType;
            private String functionName;
            private final List<ParamBean> paramList = CollUtil.newArrayList();
            private final Set<String> importTypeName = CollUtil.newHashSet();

            public void setReturnType(TypeBean returnType) {
                importTypeName.addAll(returnType.getImportJavaTypeName());
                this.returnType = returnType;
            }

            public void addParam(ParamBean param) {
                importTypeName.addAll(param.getImportTypeName());
                paramList.add(param);
            }
        }
    }

    @Data
    public static class EnumBean {
        private CommentBean doc;
        private final List<CommentBean> commentList = CollUtil.newArrayList();
        private String enumName;
        private List<EnumInstance> enumInstance = CollUtil.newArrayList();

        @Data
        public static class EnumInstance {
            private CommentBean doc;
            private final List<CommentBean> commentList = CollUtil.newArrayList();
            private String instanceName;
            private Integer instanceConstant;
        }
    }

    @Data
    public static class CommentBean {
        private CommentTypeEnum commentType;
        private String commentValue;

        public static enum CommentTypeEnum {
            LINE,
            BLOCK,
        }
    }

    @Data
    public static class TypeBean {
        public static final Map<String, String> JAVA_INTYPE_MAP = MapUtil.newHashMap();
        public static final Map<String, String> TS_INTYPE_MAP = MapUtil.newHashMap();
        public static final Map<String, String> RUST_INTYPE_MAP = MapUtil.newHashMap();
        public static final Map<String, String> SQL_INTYPE_MAP = MapUtil.newHashMap();

        static {
            JAVA_INTYPE_MAP.put(Intypes.BOOLEAN, "Boolean");
            JAVA_INTYPE_MAP.put(Intypes.BYTE, "Byte");
            JAVA_INTYPE_MAP.put(Intypes.I16, "Short");
            JAVA_INTYPE_MAP.put(Intypes.I32, "Integer");
            JAVA_INTYPE_MAP.put(Intypes.I64, "Long");
            JAVA_INTYPE_MAP.put(Intypes.SHORT, "Short");
            JAVA_INTYPE_MAP.put(Intypes.INT, "Integer");
            JAVA_INTYPE_MAP.put(Intypes.LONG, "Long");
            JAVA_INTYPE_MAP.put(Intypes.DOUBLE, "Double");
            JAVA_INTYPE_MAP.put(Intypes.BINARY, "Object");
            JAVA_INTYPE_MAP.put(Intypes.STRING, "String");

            TS_INTYPE_MAP.put(Intypes.BOOLEAN, "boolean");
            TS_INTYPE_MAP.put(Intypes.BYTE, "Blob");
            TS_INTYPE_MAP.put(Intypes.I16, "number");
            TS_INTYPE_MAP.put(Intypes.I32, "number");
            TS_INTYPE_MAP.put(Intypes.I64, "bigint");
            TS_INTYPE_MAP.put(Intypes.SHORT, "number");
            TS_INTYPE_MAP.put(Intypes.INT, "number");
            TS_INTYPE_MAP.put(Intypes.LONG, "bigint");
            TS_INTYPE_MAP.put(Intypes.DOUBLE, "number");
            TS_INTYPE_MAP.put(Intypes.BINARY, "File");
            TS_INTYPE_MAP.put(Intypes.STRING, "string");

            RUST_INTYPE_MAP.put(Intypes.BOOLEAN, "bool");
            RUST_INTYPE_MAP.put(Intypes.BYTE, "String");
            RUST_INTYPE_MAP.put(Intypes.I16, "i16");
            RUST_INTYPE_MAP.put(Intypes.I32, "i32");
            RUST_INTYPE_MAP.put(Intypes.I64, "i64");
            RUST_INTYPE_MAP.put(Intypes.SHORT, "i16");
            RUST_INTYPE_MAP.put(Intypes.INT, "i32");
            RUST_INTYPE_MAP.put(Intypes.LONG, "i64");
            RUST_INTYPE_MAP.put(Intypes.DOUBLE, "f64");
            RUST_INTYPE_MAP.put(Intypes.BINARY, "String");
            RUST_INTYPE_MAP.put(Intypes.STRING, "String");

            SQL_INTYPE_MAP.put(Intypes.BOOLEAN, "bool");
            SQL_INTYPE_MAP.put(Intypes.BYTE, "char");
            SQL_INTYPE_MAP.put(Intypes.I16, "smallint");
            SQL_INTYPE_MAP.put(Intypes.I32, "integer");
            SQL_INTYPE_MAP.put(Intypes.I64, "bigint");
            SQL_INTYPE_MAP.put(Intypes.SHORT, "smallint");
            SQL_INTYPE_MAP.put(Intypes.INT, "integer");
            SQL_INTYPE_MAP.put(Intypes.LONG, "bigint");
            SQL_INTYPE_MAP.put(Intypes.DOUBLE, "double precision");
            SQL_INTYPE_MAP.put(Intypes.BINARY, "bytea");
            SQL_INTYPE_MAP.put(Intypes.STRING, "varchar(200)");
        }

        private Modifier modifier = Modifier.REQUIRED;
        private String token;
        private String javaSimpleName;
        private String tsSimpleName;
        private String rustSimpleName;
        private String sqlSimpleName;
        private TypeBean t1;
        private TypeBean t2;
        private boolean isIntype = false;
        private boolean isMap = false;
        private boolean isCollection = false;
        private final Set<String> importJavaTypeName = CollUtil.newHashSet();

        public void setT1(TypeBean t1) {
            importJavaTypeName.addAll(t1.getImportJavaTypeName());
            this.t1 = t1;
        }

        public void setT2(TypeBean t2) {
            importJavaTypeName.addAll(t2.getImportJavaTypeName());
            this.t2 = t2;
        }

        public String javaString() {
            if (this.isMap) {
                return javaSimpleName + "<" + t1.javaString() + ", " + t2.javaString() + ">";
            } else if (this.isCollection) {
                // 对于二进制集合，我们实际上期望它的类型是byte[]，而不是List<Byte>
                if (Intypes.BYTE.equals(this.t1.getToken())) {
                    return "byte[]";
                }
                return javaSimpleName + "<" + t1.javaString() + ">";
            } else {
                return javaSimpleName;
            }
        }

        public String tsString() {
            if (this.isMap) {
                return "Record<" + t1.tsString() + ", " + t2.tsString() + ">";
            } else if (this.isCollection) {
                // 对于二进制集合，我们实际上期望它实现上传/下载功能
                if (this.getT1().getToken().equals(Intypes.BYTE)) {
                    return "string";
                } else if (this.getT1().getToken().equals(Intypes.BINARY)) {
                    return "FileList";
                }
                return t1.tsString() + "[]";
            } else if (REF_ENUM.equals(this.getToken())) {
                return this.getT1().tsString();
            } else {
                return tsSimpleName;
            }
        }

        public String rustString() {
            if (this.isMap) {
                return "std::collections::HashMap<" + t1.rustString() + ", " + t2.rustString() + ">";
            } else if (this.isCollection) {
                return "Vec<" + t1.rustString() + ">";
            } else if (REF_ENUM.equals(this.getToken())) {
                return t1.rustString();
            } else {
                return rustSimpleName;
            }
        }

        public String sqlString() {
            return sqlSimpleName;
        }
    }

    @Data
    public static class ParamBean {
        private CommentBean doc;
        private TypeBean paramType;
        private String paramName;
        private Modifier modifier;
        private final Set<String> importTypeName = CollUtil.newHashSet();

        public void setParamType(TypeBean paramType) {
            importTypeName.addAll(paramType.getImportJavaTypeName());
            this.paramType = paramType;
        }
    }

    @Data
    public static class AnnotationBean {
        private String annotationName;
        private List<String> annotationValueList = CollUtil.newArrayList();
        private final Set<String> importTypeName = CollUtil.newHashSet();
    }

    public static enum Modifier {
        REQUIRED,
        OPTIONAL;

        public static Modifier getEnumByName(String name) {
            for (Modifier value : Modifier.values()) {
                if (value.name().equalsIgnoreCase(name.trim())) {
                    return value;
                }
            }
            return null;
        }
    }

    private static class RootBeanBuilder {
        private static final String PAIRS = "pairs";
        private static final String RULE = "rule";
        private static final String INNER = "inner";

        private final RestfulDslCodeTemplateDto dto;
        private final RestfulRootBean restfulRootBean;
        private final RestfulIncludeBean restfulIncludeBean;
        private final RootBean rootBean;

        public RootBeanBuilder(RestfulRootBean restfulRootBean, RestfulIncludeBean restfulImportBean, RestfulDslCodeTemplateDto dto) {
            this.restfulRootBean = restfulRootBean;
            this.restfulIncludeBean = restfulImportBean;
            Map<String, RestfulDslCodeTemplateDto> namespaceMap = MapUtil.newHashMap();
            for (Map.Entry<String, RestfulDslCodeTemplateDto> entry : dto.getImports().entrySet()) {
                String filePath = entry.getKey();
                if (!filePath.contains(File.separator) || !filePath.contains(".")) {
                    continue;
                }
                String namespace = filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.lastIndexOf("."));
                namespaceMap.put(namespace, entry.getValue());
            }
            dto.getImports().putAll(namespaceMap);
            this.dto = dto;
            rootBean = new RootBean();
        }

        private RootBean build() {
            this.parseRoot();
            return rootBean;
        }

        private void parseRoot() {
            JacksonJsonParser parser = new JacksonJsonParser();
            Map<String, Object> rootAst = parser.parseMap(dto.getAst());
            List rootPairs = (List) rootAst.get(PAIRS);
            if (rootPairs == null || rootPairs.isEmpty()) {
                throw new RestfulDslException("根节点不能为空", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            CommentBean targetDoc = null;
            final List<CommentBean> targetCommentList = CollUtil.newArrayList();
            final Set<String> importPackage = CollUtil.newHashSet();
            final Map<String, List<String>> annotationMap = MapUtil.newHashMap();
            for (Map pairMap : (List<Map>) rootAst.get(PAIRS)) {
                String ruleName = (String) pairMap.get(RULE);
                if (pairMap.get(INNER) instanceof Map) {
                    Map innerMap = (Map) pairMap.get(INNER);
                    switch (ruleName) {
                        case COMMENT: {
                            CommentBean comment = parseComment(innerMap);
                            if (CommentBean.CommentTypeEnum.BLOCK.equals(comment.getCommentType())) {
                                targetDoc = comment;
                            } else {
                                targetCommentList.add(comment);
                            }
                            break;
                        }
                        case ANNOTATION: {
                            AnnotationBean anno = parseAnnotation(innerMap);
                            importPackage.addAll(anno.getImportTypeName());
                            annotationMap.put(anno.getAnnotationName(), anno.getAnnotationValueList());
                            break;
                        }
                        case IMPORT: {
                            targetDoc = null;
                            targetCommentList.clear();
                            importPackage.clear();
                            annotationMap.clear();
                            rootBean.addImport(parseImport(innerMap));
                            break;
                        }
                        case NAMESPACE: {
                            targetDoc = null;
                            targetCommentList.clear();
                            importPackage.clear();
                            annotationMap.clear();
                            rootBean.addNamespace(parseNamespace(innerMap));
                            break;
                        }
                        case ENUM: {
                            EnumBean enumBean = parseEnum(innerMap);
                            if (targetDoc != null) {
                                enumBean.setDoc(targetDoc);
                                targetDoc = null;
                            }
                            enumBean.getCommentList().addAll(targetCommentList);
                            targetCommentList.clear();
                            importPackage.clear();
                            annotationMap.clear();
                            rootBean.addEnum(enumBean);
                            break;
                        }
                        case CLASS: {
                            ClassBean classBean = parseClass(innerMap);
                            if (targetDoc != null) {
                                classBean.setDoc(targetDoc);
                                targetDoc = null;
                            }
                            classBean.getCommentList().addAll(targetCommentList);
                            targetCommentList.clear();
                            classBean.getImportTypeName().addAll(importPackage);
                            importPackage.clear();
                            classBean.getAnnotationMap().putAll(annotationMap);
                            annotationMap.clear();
                            rootBean.addClass(classBean);
                            break;
                        }
                        case INTERFACE: {
                            InterfaceBean interfaceBean = parseInterface(innerMap);
                            if (targetDoc != null) {
                                interfaceBean.setDoc(targetDoc);
                                targetDoc = null;
                            }
                            interfaceBean.getCommentList().addAll(targetCommentList);
                            targetCommentList.clear();
                            interfaceBean.getImportTypeName().addAll(importPackage);
                            importPackage.clear();
                            interfaceBean.getAnnotationMap().putAll(annotationMap);
                            annotationMap.clear();
                            rootBean.addInterface(interfaceBean);
                            break;
                        }
                        default: {
                            String msg = StrUtil.format("未定义的类型{}，请检查Java代码", ruleName);
                            log.error(msg);
                            throw new RestfulDslException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }
                } else if (EOI.equals(ruleName)) {
                    return;
                } else {
                    String msg = StrUtil.format("解析{}时发现未定义的inner类型，请检查Java代码", ruleName);
                    log.error(msg);
                    throw new RestfulDslException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }


        public NamespaceBean parseNamespace(Map namespaceAst) {
            NamespaceBean result = new NamespaceBean();
            for (Map pairMap : (List<Map>) namespaceAst.get(PAIRS)) {
                String ruleName = (String) pairMap.get(RULE);
                String inner = (String) pairMap.get(INNER);
                switch (ruleName) {
                    case NAMESPACE_LANG: {
                        result.setNamespaceLang(NamespaceBean.getEnumByName(inner));
                        break;
                    }
                    case NAMESPACE_VALUE: {
                        result.setNamespaceValue(inner);
                        break;
                    }
                }
            }
            return result;
        }

        public IncludeBean parseImport(Map includeAst) {
            IncludeBean result = new IncludeBean();
            for (Map pairMap : (List<Map>) includeAst.get(PAIRS)) {
                String ruleName = (String) pairMap.get(RULE);
                String inner = (String) pairMap.get(INNER);
                if (ruleName.equals(IMPORT_VALUE)) {
                    result.setIncludeValue(inner);
                }
            }
            return result;
        }

        public CommentBean parseComment(Map commentAst) {
            for (Map pairMap : (List<Map>) commentAst.get(PAIRS)) {
                CommentBean result = new CommentBean();
                String ruleName = (String) pairMap.get(RULE);
                switch (ruleName) {
                    case COMMENT_LINE: {
                        result.setCommentType(CommentBean.CommentTypeEnum.LINE);
                        result.setCommentValue((String) pairMap.get(INNER));
                        break;
                    }
                    case COMMENT_BLOCK: {
                        result.setCommentType(CommentBean.CommentTypeEnum.BLOCK);
                        result.setCommentValue((String) pairMap.get(INNER).toString().trim());
                        break;
                    }
                }
                return result;
            }
            throw new RestfulDslException("非预期的注解：\n" + JSONUtil.toJsonStr(commentAst), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        public AnnotationBean parseAnnotation(Map annoAst) {
            AnnotationBean result = new AnnotationBean();
            for (Map annoPair : (List<Map>) annoAst.get(PAIRS)) {
                String annoRule = (String) annoPair.get(RULE);
                switch (annoRule) {
                    case ANNOTATION_NAME: {
                        String annoName = (String) annoPair.get(INNER);
                        if (StrUtil.equalsIgnoreCase(annoName, "uri")) {
                            result.getImportTypeName().add("org.springframework.web.bind.annotation.RequestMapping");
                            result.setAnnotationName("RequestMapping");
                        } else if (StrUtil.equalsIgnoreCase(annoName, "postUri")) {
                            result.getImportTypeName().add("org.springframework.web.bind.annotation.PostMapping");
                            result.getImportTypeName().add("org.springframework.web.bind.annotation.RequestBody");
                            result.setAnnotationName("PostMapping");
                        } else if (StrUtil.equalsIgnoreCase(annoName, "getUri")) {
                            result.getImportTypeName().add("org.springframework.web.bind.annotation.GetMapping");
                            result.getImportTypeName().add("org.springframework.web.bind.annotation.RequestParam");
                            result.setAnnotationName("GetMapping");
                        } else if (StrUtil.equalsIgnoreCase(annoName, "putUri")) {
                            result.getImportTypeName().add("org.springframework.web.bind.annotation.PutMapping");
                            result.getImportTypeName().add("org.springframework.web.bind.annotation.RequestBody");
                            result.setAnnotationName("PutMapping");
                        } else if (StrUtil.equalsIgnoreCase(annoName, "patchUri")) {
                            result.getImportTypeName().add("org.springframework.web.bind.annotation.PatchMapping");
                            result.getImportTypeName().add("org.springframework.web.bind.annotation.RequestBody");
                            result.setAnnotationName("PatchMapping");
                        } else if (StrUtil.equalsIgnoreCase(annoName, "deleteUri")) {
                            result.getImportTypeName().add("org.springframework.web.bind.annotation.DeleteMapping");
                            result.setAnnotationName("DeleteMapping");
                        } else if (StrUtil.equalsIgnoreCase(annoName, "page")) { // 分页查询
                            result.getImportTypeName().add(PAGE_CLASS_NAME);
                            String simpleName = PAGE_CLASS_NAME.contains(".") ?
                                    PAGE_CLASS_NAME.substring(PAGE_CLASS_NAME.lastIndexOf(".") + 1) :
                                    PAGE_CLASS_NAME;
                            result.setAnnotationName(simpleName);
                        } else if (StrUtil.equalsIgnoreCase(annoName, "formData")) {
                            result.getImportTypeName().add("org.springframework.web.multipart.MultipartFile");
                            result.setAnnotationName("FormData");
                        } else if (StrUtil.equalsIgnoreCase(annoName, "request")) { //注入request
                            result.getImportTypeName().add("jakarta.servlet.http.HttpServletRequest");
                            result.setAnnotationName("HttpServletRequest");
                        } else if (StrUtil.equalsIgnoreCase(annoName, "response")) { //注入response
                            result.getImportTypeName().add("jakarta.servlet.http.HttpServletResponse");
                            result.setAnnotationName("HttpServletResponse");
                        } else {
                            result.setAnnotationName(annoName);
                            log.error("未预料的注解");
                        }
                        break;
                    }
                    case ANNOTATION_VALUE: {
                        String value = (String) annoPair.get(INNER);
                        if (StrUtil.isBlank(value)) {
                            continue;
                        }
                        if (ReUtil.contains("\\{\\s*\\w+\\s*}", value)) {
                            result.getImportTypeName().add("org.springframework.web.bind.annotation.PathVariable");
                        }
                        for (String v : StrUtil.split(value, ",")) {
                            result.getAnnotationValueList().add(v);
                        }
                        break;
                    }
                }
            }
            return result;
        }

        public EnumBean parseEnum(Map enumAst) {
            EnumBean result = new EnumBean();
            CommentBean targetDoc = null;
            final List<CommentBean> targetCommentList = CollUtil.newArrayList();
            for (Map pairMap : (List<Map>) enumAst.get(PAIRS)) {
                String ruleName = (String) pairMap.get(RULE);
                switch (ruleName) {
                    case COMMENT: {
                        CommentBean comment = parseComment((Map) pairMap.get(INNER));
                        if (CommentBean.CommentTypeEnum.BLOCK.equals(comment.getCommentType())) {
                            targetDoc = comment;
                        } else {
                            targetCommentList.add(comment);
                        }
                        break;
                    }
                    case ENUM_NAME: {
                        if (targetDoc != null) {
                            result.setDoc(targetDoc);
                            targetDoc = null;
                        }
                        if (!targetCommentList.isEmpty()) {
                            result.getCommentList().addAll(targetCommentList);
                            targetCommentList.clear();
                        }
                        result.setEnumName((String) pairMap.get(INNER));
                        break;
                    }
                    case ENUM_INSTANCE: {
                        EnumBean.EnumInstance enumInstance = parseEnumInstance((Map) pairMap.get(INNER));
                        if (targetDoc != null) {
                            enumInstance.setDoc(targetDoc);
                            targetDoc = null;
                        }
                        if (!targetCommentList.isEmpty()) {
                            enumInstance.getCommentList().addAll(targetCommentList);
                            targetCommentList.clear();
                        }
                        result.getEnumInstance().add(enumInstance);
                        break;
                    }
                }
            }
            return result;
        }

        public EnumBean.EnumInstance parseEnumInstance(Map enumInstanceAst) {
            EnumBean.EnumInstance result = new EnumBean.EnumInstance();
            for (Map pairMap : (List<Map>) enumInstanceAst.get(PAIRS)) {
                String ruleName = (String) pairMap.get(RULE);
                switch (ruleName) {
                    case ENUM_INSTANCE_CONSTANT: {
                        result.setInstanceConstant(Integer.parseInt((String) pairMap.get(INNER)));
                        break;
                    }
                    case ENUM_INSTANCE_NAME: {
                        result.setInstanceName((String) pairMap.get(INNER));
                        break;
                    }
                }
            }
            return result;
        }

        public ClassBean parseClass(Map structAst) {
            ClassBean result = new ClassBean();
            CommentBean targetDoc = null;
            final List<CommentBean> targetCommentList = CollUtil.newArrayList();
            for (Map pairMap : (List<Map>) structAst.get(PAIRS)) {
                String ruleName = (String) pairMap.get(RULE);
                switch (ruleName) {
                    case COMMENT: {
                        CommentBean comment = parseComment((Map) pairMap.get(INNER));
                        if (CommentBean.CommentTypeEnum.BLOCK.equals(comment.getCommentType())) {
                            targetDoc = comment;
                        } else {
                            targetCommentList.add(comment);
                        }
                        break;
                    }
                    case CLASS_NAME: {
                        if (targetDoc != null) {
                            result.setDoc(targetDoc);
                            targetDoc = null;
                        }
                        if (!targetCommentList.isEmpty()) {
                            result.getCommentList().addAll(targetCommentList);
                            targetCommentList.clear();
                        }
                        result.setClassName((String) pairMap.get(INNER));
                        break;
                    }
                    case CLASS_FIELD: {
                        ClassBean.ClassFieldBean classField = parseClassField((Map) pairMap.get(INNER));
                        if (targetDoc != null) {
                            classField.setDoc(targetDoc);
                            targetDoc = null;
                        }
                        if (!targetCommentList.isEmpty()) {
                            classField.getCommentList().addAll(targetCommentList);
                            targetCommentList.clear();
                        }
                        result.addClassField(classField);
                        break;
                    }
                }
            }
            return result;
        }

        public ClassBean.ClassFieldBean parseClassField(Map classFieldAst) {
            ClassBean.ClassFieldBean result = new ClassBean.ClassFieldBean();
            Modifier modifier = Modifier.REQUIRED;
            for (Map pairMap : (List<Map>) classFieldAst.get(PAIRS)) {
                String ruleName = (String) pairMap.get(RULE);
                switch (ruleName) {
                    case MODIFIER: {
                        modifier = Modifier.getEnumByName((String) pairMap.get(INNER));
                        break;
                    }
                    case CONTAIN_LIST: {
                        result.setType(parseContainList((Map) pairMap.get(INNER), modifier));
                        break;
                    }
                    case CONTAIN_MAP: {
                        result.setType(parseContainMap((Map) pairMap.get(INNER), modifier));
                        break;
                    }
                    case CONTAIN_SET: {
                        result.setType(parseContainSet((Map) pairMap.get(INNER), modifier));
                        break;
                    }
                    case REF_ENUM: {
                        result.setType(parseRefEnum((Map) pairMap.get(INNER), modifier));
                        break;
                    }
                    case INTYPE: {
                        result.setType(parseIntype((String) pairMap.get(INNER), modifier));
                        break;
                    }
                    case UTYPE: {
                        result.setType(parseUtype((Map) pairMap.get(INNER), modifier));
                        break;
                    }
                    case CLASS_FIELD_NAME: {
                        result.setFieldName((String) pairMap.get(INNER));
                        break;
                    }
                }
            }
            result.setModifier(modifier);
            if (Modifier.OPTIONAL.equals(modifier)) {
                result.getImportTypeName().add(Nullable.class.getName());
            }
            return result;
        }

        public InterfaceBean parseInterface(Map interfaceAst) {
            InterfaceBean result = new InterfaceBean();
            CommentBean targetDoc = null;
            final List<CommentBean> targetCommentList = CollUtil.newArrayList();
            final Set<String> functionImportPackage = CollUtil.newHashSet();
            final Map<String, List<String>> annoMap = MapUtil.newHashMap();
            for (Map pairMap : (List<Map>) interfaceAst.get(PAIRS)) {
                String ruleName = (String) pairMap.get(RULE);
                switch (ruleName) {
                    case COMMENT: {
                        CommentBean comment = parseComment((Map) pairMap.get(INNER));
                        if (CommentBean.CommentTypeEnum.BLOCK.equals(comment.getCommentType())) {
                            targetDoc = comment;
                        } else {
                            targetCommentList.add(comment);
                        }
                        break;
                    }
                    case ANNOTATION: {
                        AnnotationBean anno = parseAnnotation((Map) pairMap.get(INNER));
                        result.getImportTypeName().addAll(anno.getImportTypeName());
                        annoMap.put(anno.getAnnotationName(), anno.getAnnotationValueList());
                        break;
                    }
                    case INTERFACE_NAME: {
                        result.setInterfaceName((String) pairMap.get(INNER));
                        break;
                    }
                    case INTERFACE_FUNCTION: {
                        InterfaceBean.InterfaceFunctionBean interfaceFunction = parseInterfaceFunction((Map) pairMap.get(INNER));
                        if (targetDoc != null) {
                            interfaceFunction.setDoc(targetDoc);
                            targetDoc = null;
                        }
                        interfaceFunction.getCommentList().addAll(targetCommentList);
                        targetCommentList.clear();
                        interfaceFunction.getImportTypeName().addAll(functionImportPackage);
                        functionImportPackage.clear();
                        interfaceFunction.getAnnotationMap().putAll(annoMap);
                        annoMap.clear();
                        result.addInterfaceFunction(interfaceFunction);
                        break;
                    }
                }
            }
            return result;
        }

        public InterfaceBean.InterfaceFunctionBean parseInterfaceFunction(Map interfaceFunctionAst) {
            InterfaceBean.InterfaceFunctionBean result = new InterfaceBean.InterfaceFunctionBean();
            CommentBean doc = null;
            for (Map pairMap : (List<Map>) interfaceFunctionAst.get(PAIRS)) {
                String ruleName = (String) pairMap.get(RULE);
                switch (ruleName) {
                    case TYPE: {
                        result.setReturnType(Objects.requireNonNull(parseType((Map) pairMap.get(INNER), Modifier.REQUIRED)));
                        break;
                    }
                    case VOID: { // 没有返回值
                        TypeBean returnType = new TypeBean();
                        returnType.setToken("void");
                        returnType.setJavaSimpleName("void");
                        returnType.setTsSimpleName("null");
                        returnType.setRustSimpleName("()");
                        result.setReturnType(returnType);
                        break;
                    }
                    case INTERFACE_FUNCTION_NAME: {
                        result.setFunctionName((String) pairMap.get(INNER));
                        break;
                    }
                    case COMMENT: {
                        CommentBean comment = parseComment((Map) pairMap.get(INNER));
                        if (CommentBean.CommentTypeEnum.BLOCK.equals(comment.getCommentType())) {
                            doc = comment;
                        }
                        break;
                    }
                    case PARAM: {
                        result.getImportTypeName().add("io.swagger.v3.oas.annotations.Parameter");
                        ParamBean param = parseParam((Map) pairMap.get(INNER));
                        if (Modifier.OPTIONAL.equals(param.getModifier())) {
                            result.getImportTypeName().add(Nullable.class.getName());
                        }
                        if (doc != null) {
                            param.setDoc(doc);
                        }
                        doc = null;
                        result.addParam(param);
                        break;
                    }
                }
            }
            return result;
        }

        public ParamBean parseParam(Map paramAst) {
            ParamBean result = new ParamBean();
            Modifier modifier = Modifier.REQUIRED;
            for (Map pairMap : (List<Map>) paramAst.get(PAIRS)) {
                String ruleName = (String) pairMap.get(RULE);
                switch (ruleName) {
                    case MODIFIER: {
                        modifier = Modifier.getEnumByName((String) pairMap.get(INNER));
                        break;
                    }
                    case TYPE: {
                        result.setParamType(Objects.requireNonNull(parseType((Map) pairMap.get(INNER), modifier)));
                        break;
                    }
                    case PARAM_NAME: {
                        result.setParamName((String) pairMap.get(INNER));
                        break;
                    }
                }
            }
            result.setModifier(modifier);
            return result;
        }

        public TypeBean parseType(Map typeAst, Modifier modifier) {
            for (Map pairMap : (List<Map>) typeAst.get(PAIRS)) {
                String ruleName = (String) pairMap.get(RULE);
                TypeBean result;

                switch (ruleName) {
                    case INTYPE: {
                        result = parseIntype((String) pairMap.get(INNER), modifier);
                        break;
                    }
                    case CONTAIN_MAP: {
                        result = parseContainMap((Map) pairMap.get(INNER), modifier);
                        break;
                    }
                    case CONTAIN_LIST: {
                        result = parseContainList((Map) pairMap.get(INNER), modifier);
                        break;
                    }
                    case CONTAIN_SET: {
                        result = parseContainSet((Map) pairMap.get(INNER), modifier);
                        break;
                    }
                    case REF_ENUM: {
                        result = parseRefEnum((Map) pairMap.get(INNER), modifier);
                        break;
                    }
                    case UTYPE: {
                        result = parseUtype((Map) pairMap.get(INNER), modifier);
                        break;
                    }
                    default:
                        throw new RestfulDslException("unknown type rule: " + ruleName, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if (Modifier.OPTIONAL.equals(modifier)) {
                    result.getImportJavaTypeName().add(Nullable.class.getName());
                }
                return result;
            }
            return null;
        }

        public TypeBean parseIntype(String intypeString, Modifier modifier) {
            TypeBean result = new TypeBean();
            result.setIntype(true);
            result.setToken(intypeString);
            result.setModifier(modifier);
            result.setJavaSimpleName(TypeBean.JAVA_INTYPE_MAP.get(intypeString));
            result.setTsSimpleName(TypeBean.TS_INTYPE_MAP.get(intypeString));
            result.setRustSimpleName(TypeBean.RUST_INTYPE_MAP.get(intypeString));
            result.setSqlSimpleName(TypeBean.SQL_INTYPE_MAP.get(intypeString));
            return result;
        }

        public TypeBean parseContainMap(Map ast, Modifier modifier) {
            TypeBean result = new TypeBean();
            result.setToken(CONTAIN_MAP);
            result.setModifier(modifier);
            result.setJavaSimpleName("Map");
            result.setTsSimpleName("Record");
            result.setRustSimpleName("std::collections::HashMap");
            result.setSqlSimpleName("jsonb");
            result.setMap(true);
            result.getImportJavaTypeName().add(Map.class.getName());
            for (Map innerMap : (List<Map>) ast.get(PAIRS)) {
                String innerRuleName = (String) innerMap.get(RULE);
                if (CONTAIN_MAP_KEYTYPE.equals(innerRuleName)) {
                    Map lastMap = (Map) ((List) ((Map) innerMap.get(INNER)).get(PAIRS)).get(0);
                    result.setT1(Objects.requireNonNull(parseType((Map) lastMap.get(INNER), Modifier.REQUIRED)));
                } else if (CONTAIN_MAP_VALUETYPE.equals(innerRuleName)) {
                    Map lastMap = (Map) ((List) ((Map) innerMap.get(INNER)).get(PAIRS)).get(0);
                    result.setT2(Objects.requireNonNull(parseType((Map) lastMap.get(INNER), Modifier.REQUIRED)));
                }
            }
            return result;
        }

        public TypeBean parseContainList(Map ast, Modifier modifier) {
            TypeBean result = new TypeBean();
            result.setToken(CONTAIN_LIST);
            result.setJavaSimpleName("List");
            result.setTsSimpleName("");
            result.setRustSimpleName("Vec");
            result.setSqlSimpleName("jsonb");
            result.setCollection(true);
            result.getImportJavaTypeName().add(List.class.getName());
            for (Map innerMap : (List<Map>) ast.get(PAIRS)) {
                String innerRuleName = (String) innerMap.get(RULE);
                if (CONTAIN_LIST_TYPE.equals(innerRuleName)) {
                    Map lastMap = (Map) ((List) ((Map) innerMap.get(INNER)).get(PAIRS)).get(0);
                    result.setT1(Objects.requireNonNull(parseType((Map) lastMap.get(INNER), Modifier.REQUIRED)));
                }
            }
            return result;
        }

        public TypeBean parseContainSet(Map ast, Modifier modifier) {
            TypeBean result = new TypeBean();
            result.setModifier(modifier);
            result.setToken(CONTAIN_SET);
            result.setJavaSimpleName("Set");
            result.setTsSimpleName("");
            result.setRustSimpleName("Vec");
            result.setSqlSimpleName("jsonb");
            result.setCollection(true);
            result.getImportJavaTypeName().add(Set.class.getName());
            for (Map innerMap : (List<Map>) ast.get(PAIRS)) {
                String innerRuleName = (String) innerMap.get(RULE);
                if (CONTAIN_SET_TYPE.equals(innerRuleName)) {
                    Map lastMap = (Map) ((List) ((Map) innerMap.get(INNER)).get(PAIRS)).get(0);
                    result.setT1(Objects.requireNonNull(parseType((Map) lastMap.get(INNER), Modifier.REQUIRED)));
                }
            }
            return result;
        }

        public TypeBean parseRefEnum(Map ast, Modifier modifier) {
            TypeBean result = new TypeBean();
            result.setToken(REF_ENUM);
//            if (Modifier.REQUIRED.equals(modifier)) {
//                result.setJavaSimpleName("int");
//            } else {
            result.setJavaSimpleName("Integer");
//            }
            result.setTsSimpleName("number");
            result.setRustSimpleName("i32");
            result.setSqlSimpleName("integer");
            result.setIntype(true);
            for (Map innerMap : (List<Map>) ast.get(PAIRS)) {
                String innerRuleName = (String) innerMap.get(RULE);
                if (UTYPE.equals(innerRuleName)) {
                    result.setT1(parseUtype((Map) innerMap.get(INNER), Modifier.REQUIRED));
                }
            }
            return result;
        }

        public TypeBean parseUtype(Map ast, Modifier modifier) {
            TypeBean result = new TypeBean();
            String importJavaPackage = null;
            String importTsTypeName = "";
            String importRustTypeName = "";
            for (Map innerMap : (List<Map>) ast.get(PAIRS)) {
                String innerRuleName = (String) innerMap.get(RULE);
                if (UTYPE_NAMESPACE.equals(innerRuleName)) {
                    String namespace = (String) innerMap.get(INNER);
                    importTsTypeName += namespace + ".";
                    importRustTypeName += StrUtil.toUnderlineCase(namespace) + "::";
                    RestfulIncludeBean restfulIncludeBean = restfulRootBean.getRestfulIncludeBeanByFileName(namespace);
                    if (restfulIncludeBean == null) {
                        RestfulDslCodeTemplateDto includeDto = restfulRootBean.getIncludeDtoMap().get(namespace);
                        restfulIncludeBean = new RestfulIncludeBean(restfulRootBean, includeDto);
                        restfulRootBean.getIncludeBeanSet().add(restfulIncludeBean);
                    }
                    String javaPackageName = restfulIncludeBean.getRootBean().getJavaNameSpace();
                    if (javaPackageName == null) {
                        throw new RestfulDslException("import数据类型有误，请检查", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    importJavaPackage = javaPackageName;
                } else if (UTYPE_CUSTOMNAME.equals(innerRuleName)) {
                    String includeValue = (String) innerMap.get(INNER);
                    importTsTypeName += includeValue;
                    importRustTypeName += includeValue;
                    if (importJavaPackage != null) {
                        result.getImportJavaTypeName().add(importJavaPackage + "." + includeValue);
                    }
                    result.setJavaSimpleName(includeValue);
//                    注释原因：在java中同一个包的不同类之间不需要import
//                    if (importJavaPackage != null) {
//                        importJavaPackage = rootBean.getJavaNameSpace();
//                    }
                }
            }
            result.setTsSimpleName(importTsTypeName);
            result.setRustSimpleName(importRustTypeName);
            result.setSqlSimpleName("jsonb");
            result.setToken(UTYPE);
            return result;
        }
    }

    public interface RestfulRootIface {
        RootBean getRootBean();

        String getFileName();

        String getFilePath();
    }
}
