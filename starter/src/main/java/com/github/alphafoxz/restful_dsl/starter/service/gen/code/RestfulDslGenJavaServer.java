package com.github.alphafoxz.restful_dsl.starter.service.gen.code;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.github.alphafoxz.restful_dsl.starter.RestfulDslConstants;
import com.github.alphafoxz.restful_dsl.starter.configuration.RestfulDslProperties;
import com.github.alphafoxz.restful_dsl.starter.exception.RestfulDslException;
import com.github.alphafoxz.restful_dsl.starter.toolkit.ParseRestfulSyntaxTreeUtil;
import com.github.alphafoxz.restful_dsl.starter.toolkit.RestfulTokenDefine;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

@Slf4j
@Service
public class RestfulDslGenJavaServer implements RestfulCodeGenerator {
    private static final String TAB = "    ";
    private static final String PAGE_CLASS_NAME = SpringUtil.getBean(RestfulDslProperties.class).getPageClass();
    private final List<String> postAnnoList = CollUtil.newArrayList("PostMapping", "PutMapping", "PatchMapping");
    @Resource
    private RestfulDslProperties restfulDslProperties;

    @Override
    public Set<CodeFile> genCodeFileSet(@NonNull ParseRestfulSyntaxTreeUtil.RestfulRootBean restfulRoot, String targetDir) {
        Set<CodeFile> result = CollUtil.newHashSet();
        generateRestJavaIfaces(restfulRoot, result);
        generateRestJavaEnums(restfulRoot, result);
        generateRestJavaDtos(restfulRoot, result);
        for (ParseRestfulSyntaxTreeUtil.RestfulIncludeBean includeBean : restfulRoot.getIncludeBeanSet()) {
            generateRestJavaIfaces(includeBean, result);
            generateRestJavaDtos(includeBean, result);
            generateRestJavaEnums(includeBean, result);
        }
        return result;
    }

    private CodeFile genApiFile(ParseRestfulSyntaxTreeUtil.RestfulRootIface restfulRoot, ParseRestfulSyntaxTreeUtil.InterfaceBean interfaceBean) {
        ParseRestfulSyntaxTreeUtil.RootBean rootBean = restfulRoot.getRootBean();
        CodeFile codeFile = new CodeFile();
        StringJoiner code = new StringJoiner("\n");
        code.add("package " + rootBean.getNamespaceMap().get(ParseRestfulSyntaxTreeUtil.NamespaceBean.NamespaceLangEnum.JAVA) + ";\n");
        code.add("import io.swagger.v3.oas.annotations.Operation;");
        code.add("import io.swagger.v3.oas.annotations.media.Content;");
        code.add("import io.swagger.v3.oas.annotations.media.Schema;");
        code.add("import io.swagger.v3.oas.annotations.responses.ApiResponse;");
        code.add("import io.swagger.v3.oas.annotations.tags.Tag;");
        code.add("import org.springframework.http.ResponseEntity;");
        code.add("");
        code.add("import " + restfulDslProperties.getHttpControllerClass() + ";");
        for (String str : interfaceBean.getImportTypeName()) {
            code.add("import " + str + ";");
        }
        code.add("");
        {
            //解析普通注释
            for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : interfaceBean.getCommentList()) {
                code.add("// " + commentBean.getCommentValue().trim());
            }
            //解析@interface注解
            for (Map.Entry<String, List<String>> stringListEntry : interfaceBean.getAnnotationMap().entrySet()) {
                String annoCode = "@" + stringListEntry.getKey();
                if (CollUtil.isNotEmpty(stringListEntry.getValue())) {
                    StringJoiner valueJoiner = new StringJoiner("\", \"", "({\"", "\"})");
                    valueJoiner.setEmptyValue("");
                    for (String s : stringListEntry.getValue()) {
                        valueJoiner.add(s);
                    }
                    annoCode += valueJoiner;
                }
                code.add(annoCode);
            }
            //解析API注释
            ParseRestfulSyntaxTreeUtil.CommentBean interfaceDoc = interfaceBean.getDoc();
            code.add(StrUtil.format("@Tag(name = {}, description = {})", JSONUtil.quote(interfaceBean.getInterfaceName(), true), commentDocToStringWrapParam(interfaceDoc)));
        }
        String simpleName = restfulDslProperties.getHttpControllerClass().contains(".") ?
                restfulDslProperties.getHttpControllerClass().substring(restfulDslProperties.getHttpControllerClass().lastIndexOf(".") + 1) :
                restfulDslProperties.getHttpControllerClass();
        code.add(StrUtil.format("public interface {} extends " + simpleName + " {", interfaceBean.getInterfaceName()));
        {
            //解析接口方法
            for (ParseRestfulSyntaxTreeUtil.InterfaceBean.InterfaceFunctionBean interfaceFunction : interfaceBean.getInterfaceFunctionList()) {
                if (interfaceFunction.getAnnotationMap().get("RequestMapping") != null) {
                    throw new RestfulDslException("@uri不允许注解在具体方法上，必须指定一个特定的http方法，请使用@postUri或@getUri", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                code.add(genInterfaceFunctionCode(interfaceFunction));
            }
        }
        code.add("}");
        codeFile.setContent(code.toString());
        codeFile.setPath(getRestGeneratePath(restfulRoot, interfaceBean.getInterfaceName()));
        codeFile.setTemplatePath(restfulRoot.getFilePath());
        codeFile.setFileName(interfaceBean.getInterfaceName() + ".java");
        return codeFile;
    }

    private void generateRestJavaIfaces(ParseRestfulSyntaxTreeUtil.RestfulRootIface restfulRoot, @NonNull Set<CodeFile> result) throws RestfulDslException {
        ParseRestfulSyntaxTreeUtil.RootBean rootBean = restfulRoot.getRootBean();
        for (ParseRestfulSyntaxTreeUtil.InterfaceBean interfaceBean : rootBean.getInterfaceList()) {
            result.add(genApiFile(restfulRoot, interfaceBean));
        }
    }

    private CodeFile genEnumFile(ParseRestfulSyntaxTreeUtil.RestfulRootIface restfulRoot, ParseRestfulSyntaxTreeUtil.EnumBean enumBean) {
        ParseRestfulSyntaxTreeUtil.RootBean rootBean = restfulRoot.getRootBean();
        CodeFile codeFile = new CodeFile();
        StringJoiner code = new StringJoiner("\n");
        code.add("package " + rootBean.getNamespaceMap().get(ParseRestfulSyntaxTreeUtil.NamespaceBean.NamespaceLangEnum.JAVA) + ";\n");
        code.add("import io.swagger.v3.oas.annotations.media.Schema;");
        code.add("import lombok.AllArgsConstructor;");
        code.add("import lombok.Getter;\n");
        // 生成Enum的注释
        code.add(StrUtil.format("@Schema(description = {})", commentDocToStringWrapParam(enumBean.getDoc())));
        code.add("@AllArgsConstructor");
        code.add("@Getter");
        if (enumBean.getCommentList() != null) {
            for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : enumBean.getCommentList()) {
                code.add("// " + commentBean.getCommentValue());
            }
        }
        code.add("public enum " + enumBean.getEnumName() + " {");
        StringJoiner enumJoiner = new StringJoiner(",\n", "", ";");
        for (ParseRestfulSyntaxTreeUtil.EnumBean.EnumInstance enumInstance : enumBean.getEnumInstance()) {
            String instStr = "";
            //生成枚举的注释
            for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : enumInstance.getCommentList()) {
                instStr += TAB + "// " + commentBean.getCommentValue() + '\n';
            }
            if (enumInstance.getDoc() != null) {
                instStr += TAB + "/**" + enumInstance.getDoc().getCommentValue() + "*/\n";
            }
            instStr += TAB + enumInstance.getInstanceName() + "(" + enumInstance.getInstanceConstant() + ")";
            enumJoiner.add(instStr);
        }
        code.add(enumJoiner.toString());
        code.add("");
        code.add(TAB + "private final int value;");
        code.add("}");
        codeFile.setContent(code.toString());
        codeFile.setPath(getRestGeneratePath(restfulRoot, enumBean.getEnumName()));
        codeFile.setTemplatePath(restfulRoot.getFilePath());
        codeFile.setFileName(enumBean.getEnumName() + ".java");
        return codeFile;
    }

    private void generateRestJavaEnums(ParseRestfulSyntaxTreeUtil.RestfulRootIface restfulRoot, @NonNull Set<CodeFile> result) throws RestfulDslException {
        ParseRestfulSyntaxTreeUtil.RootBean rootBean = restfulRoot.getRootBean();
        for (ParseRestfulSyntaxTreeUtil.EnumBean enumBean : rootBean.getEnumList()) {
            result.add(genEnumFile(restfulRoot, enumBean));
        }
    }

    private CodeFile genDtoFile(ParseRestfulSyntaxTreeUtil.RestfulRootIface restfulRoot, ParseRestfulSyntaxTreeUtil.ClassBean classBean) {
        ParseRestfulSyntaxTreeUtil.RootBean rootBean = restfulRoot.getRootBean();
        CodeFile codeFile = new CodeFile();
        StringJoiner code = new StringJoiner("\n");
        code.add("package " + rootBean.getNamespaceMap().get(ParseRestfulSyntaxTreeUtil.NamespaceBean.NamespaceLangEnum.JAVA) + ";\n");
        code.add("import io.swagger.v3.oas.annotations.media.Schema;");
        code.add("import lombok.Getter;");
        code.add("import lombok.Setter;");
        code.add("import lombok.experimental.Accessors;");
        for (String str : classBean.getImportTypeName()) {
            code.add("import " + str + ";");
        }
        code.add("");
        {
            //解析普通注释
            List<ParseRestfulSyntaxTreeUtil.CommentBean> commentList = classBean.getCommentList();
            if (CollUtil.isNotEmpty(commentList)) {
                for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : commentList) {
                    code.add("// " + commentBean.getCommentValue().trim());
                }
            }
        }
        {
            //解析注解注释
            ParseRestfulSyntaxTreeUtil.CommentBean classDoc = classBean.getDoc();
            code.add(StrUtil.format("@Schema(name = {}, description = {})", JSONUtil.quote(classBean.getClassName(), true), commentDocToStringWrapParam(classDoc)));
        }
        code.add("@Accessors(chain = true)");
        code.add("@Getter");
        code.add("@Setter");
        code.add("public class " + classBean.getClassName() + " {");
        for (ParseRestfulSyntaxTreeUtil.ClassBean.ClassFieldBean fieldBean : classBean.getClassFieldList()) {
            if (CollUtil.isNotEmpty(fieldBean.getCommentList())) {
                for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : fieldBean.getCommentList()) {
                    code.add(TAB + "//" + commentBean.getCommentValue().trim());
                }
            }
            if (RestfulTokenDefine.REF_ENUM.equals(fieldBean.getType().getToken())) {
                code.add(TAB + "/**");
                code.add(TAB + " * @see " + fieldBean.getType().getT1().javaString());
                code.add(TAB + " */");
            }
            code.add(StrUtil.format(TAB + "@Schema(name = {}, description = {})", JSONUtil.quote(fieldBean.getFieldName(), true), commentDocToStringWrapParam(fieldBean.getDoc())));
            if (ParseRestfulSyntaxTreeUtil.Modifier.OPTIONAL.equals(fieldBean.getModifier())) {
                code.add(TAB + "@Nullable");
            }
            code.add(TAB + "private " + fieldBean.getType().javaString() + " " + fieldBean.getFieldName() + ";");
        }
        code.add("}");

        codeFile.setContent(code.toString());
        codeFile.setPath(getRestGeneratePath(restfulRoot, classBean.getClassName()));
        codeFile.setTemplatePath(restfulRoot.getFilePath());
        codeFile.setFileName(classBean.getClassName() + ".java");
        return codeFile;
    }

    /**
     * 本方法包含的特殊处理：
     * 由于post方式请求体为json对象，但是当参数很简单或者复用的可能性极低时，为这种参数额外建模纯属于无效工作量
     * 所以就将其转化为一个单个参数的接口，用java.util.Map接收参数
     */
    private String genInterfaceFunctionCode(ParseRestfulSyntaxTreeUtil.InterfaceBean.InterfaceFunctionBean interfaceFunction) {
        StringJoiner code = new StringJoiner("\n");
        // 是否分页
        boolean isPage = false;
        boolean isPost = false;
        boolean isFormData = false;
        boolean hasRequestParam = false;
        boolean hasResponseParam = false;
        StringJoiner refEnumDesc = new StringJoiner("\n" + TAB + " * ", TAB + "/* \n" + TAB + " * ", "\n" + TAB + " */\n");
        refEnumDesc.setEmptyValue("");
        Set<String> pathVarSet = CollUtil.newHashSet();
        {
            //解析普通注释
            List<ParseRestfulSyntaxTreeUtil.CommentBean> commentList = interfaceFunction.getCommentList();
            if (CollUtil.isNotEmpty(commentList)) {
                for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : commentList) {
                    code.add(TAB + "// " + commentBean.getCommentValue().trim());
                }
            }
            //处理枚举
            for (ParseRestfulSyntaxTreeUtil.ParamBean paramBean : interfaceFunction.getParamList()) {
                if (RestfulTokenDefine.REF_ENUM.equals(paramBean.getParamType().getToken())) {
                    refEnumDesc.add("@param " + paramBean.getParamName() + " 枚举值");
                    refEnumDesc.add("@see " + paramBean.getParamType().getT1().javaString());
                }
            }
            //解析@interface注解
            for (Map.Entry<String, List<String>> annoEntry : interfaceFunction.getAnnotationMap().entrySet()) {
                switch (annoEntry.getKey()) {
                    case "Page": {
                        isPage = true;
                        continue;
//                        break;
                    }
                    case "FormData": {
                        isFormData = true;
                        continue;
//                        break;
                    }
                    case "HttpServletRequest": {
                        hasRequestParam = true;
                        continue;
//                        break;
                    }
                    case "HttpServletResponse": {
                        hasResponseParam = true;
                        continue;
//                        break;
                    }
                }
                String annoCode = TAB + "@" + annoEntry.getKey();
                if (CollUtil.isNotEmpty(annoEntry.getValue())) {
                    StringJoiner valueJoiner = new StringJoiner("\", \"", "(value = {\"", isFormData ? "\"}, consumes = \"multipart/form-data\")" : "\"})");
                    valueJoiner.setEmptyValue("");
                    for (String s : annoEntry.getValue()) {
                        List<String> all = ReUtil.findAll("\\{\\s*\\w+\\s*}", s, 0);
                        for (String pathVar : all) {
                            pathVarSet.add(pathVar.substring(1, pathVar.length() - 1).trim());
                        }
                        valueJoiner.add(s);
                    }
                    annoCode += valueJoiner;
                }
                if (CollUtil.contains(postAnnoList, annoEntry.getKey())) {
                    isPost = true;
                }
                code.add(annoCode);
            }
            //解析API注释
            ParseRestfulSyntaxTreeUtil.CommentBean functionDoc = interfaceFunction.getDoc();
            String formatStr = refEnumDesc + TAB + "@Operation(summary = {}, responses = {\n"
                    + TAB + TAB + TAB + "@ApiResponse(description = \"请求成功\", responseCode = \"200\", content = @Content(mediaType = \"application/json\", schema = @Schema(implementation = String.class))),\n"
                    + TAB + TAB + TAB + "@ApiResponse(description = \"无权限\", responseCode = \"403\", content = @Content(schema = @Schema(hidden = true))),\n"
                    + TAB + TAB + TAB + "@ApiResponse(description = \"参数无效\", responseCode = \"400\", content = @Content(schema = @Schema(hidden = true))),\n"
                    + TAB + "})";
            code.add(StrUtil.format(formatStr, commentDocToStringWrapParam(functionDoc)));
        }
        String returnType = interfaceFunction.getReturnType().javaString();
        if ("void".equals(returnType)) {
            returnType = "?";
        }
        if (isPage) {
            String pageSimpleName = "Page";
            if (StrUtil.contains(PAGE_CLASS_NAME, ".")) {
                pageSimpleName = PAGE_CLASS_NAME.substring(PAGE_CLASS_NAME.lastIndexOf(".") + 1);
            }
            returnType = pageSimpleName + "<" + returnType + ">";
        }
        returnType = "ResponseEntity<" + returnType + ">";
        if (isPost
                && !isFormData
                && !interfaceFunction.getParamList().isEmpty()
                && (interfaceFunction.getParamList().size() > 1 || interfaceFunction.getParamList().get(0).getParamType().isIntype())) {
            // NOTE 对多个参数的post方法进行特殊处理
            StringJoiner outerParamCode1 = new StringJoiner(",\n", "\n", "\n" + TAB);
            outerParamCode1.add(TAB + TAB + "@RequestBody java.util.Map<String, Object> _requestMap");
            StringJoiner outerParamCode2 = new StringJoiner(",\n", "\n", "\n" + TAB);
            StringJoiner innerParamCode = new StringJoiner(", ");
            for (ParseRestfulSyntaxTreeUtil.ParamBean param : interfaceFunction.getParamList()) {
                String paramAnno = "";
                if (ParseRestfulSyntaxTreeUtil.Modifier.OPTIONAL.equals(param.getModifier())) {
                    paramAnno += "@Nullable ";
                }
                outerParamCode2.add(StrUtil.format(TAB + TAB + TAB + "{}{} {}", paramAnno, param.getParamType().javaString(), param.getParamName()));
                if (param.getParamType().isIntype()) {
                    innerParamCode.add(StrUtil.format("({}) _requestMap.get(\"{}\")", param.getParamType().javaString(), param.getParamName()));
                } else {
                    innerParamCode.add(StrUtil.format("U.toBean(_requestMap.get(\"{}\"), {}.class)", param.getParamName(), param.getParamType().javaString()));
                }
            }
            if (hasRequestParam) {
                outerParamCode1.add(TAB + TAB + TAB + "HttpServletRequest _request");
                innerParamCode.add("_request");
                outerParamCode2.add(TAB + TAB + TAB + "HttpServletRequest _request");
            }
            if (hasResponseParam) {
                outerParamCode1.add(TAB + TAB + TAB + "HttpServletResponse _response");
                innerParamCode.add("_response");
                outerParamCode2.add(TAB + TAB + TAB + "HttpServletResponse _response");
            }
            String format = TAB + "public default {} {}({}) {\n" +
                    TAB + TAB + "return {}({});\n" +
                    TAB + "}\n" +
                    refEnumDesc +
                    TAB + "public {} {}({});\n";
            code.add(StrUtil.format(format,
                    returnType,
                    interfaceFunction.getFunctionName(),
                    outerParamCode1.toString(),
                    interfaceFunction.getFunctionName(),
                    innerParamCode,
                    returnType,
                    interfaceFunction.getFunctionName(),
                    outerParamCode2
            ));
            return code.toString();
        }
        StringJoiner paramCode = new StringJoiner(",\n", "\n", "\n" + TAB);
        for (ParseRestfulSyntaxTreeUtil.ParamBean param : interfaceFunction.getParamList()) {
            String format = isPost && !isFormData ? TAB + TAB + TAB + "@Parameter(description = {}) @RequestBody {}{} {}" : TAB + TAB + TAB + "@Parameter(description = {}) {}{} {}";
            String paramAnno = "";
            if (ParseRestfulSyntaxTreeUtil.Modifier.OPTIONAL.equals(param.getModifier())) {
                paramAnno += "@Nullable ";
            }
            if (pathVarSet.contains(param.getParamName())) {
                paramAnno += "@PathVariable(\"" + param.getParamName() + "\") ";
            } else if (!isPost) {
                paramAnno += "@RequestParam ";
            }
            String paramType;
            if (isFormData
                    && (param.getParamType().isCollection() && ParseRestfulSyntaxTreeUtil.Intypes.BINARY.equals(param.getParamType().getT1().getToken())
                    || ParseRestfulSyntaxTreeUtil.Intypes.BINARY.equals(param.getParamType().getToken()))
            ) {
                // 传参为二进制单文件或多文件
                paramType = "MultipartFile";
            } else {
                paramType = param.getParamType().javaString();
            }
            paramCode.add(StrUtil.format(format, commentDocToStringWrapParam(param.getDoc()), paramAnno, paramType, param.getParamName()));
        }
        if (hasRequestParam) {
            paramCode.add(TAB + TAB + TAB + "HttpServletRequest _request");
        }
        if (hasResponseParam) {
            paramCode.add(TAB + TAB + TAB + "HttpServletResponse _response");
        }
        String format = TAB + "public {} {}({});\n";
        code.add(StrUtil.format(format,
                returnType,
                interfaceFunction.getFunctionName(),
                (!interfaceFunction.getParamList().isEmpty() ? paramCode.toString() : "")
        ));
        return code.toString();
    }

    private void generateRestJavaDtos(ParseRestfulSyntaxTreeUtil.RestfulRootIface restfulRoot, Set<CodeFile> result) throws RestfulDslException {
        ParseRestfulSyntaxTreeUtil.RootBean rootBean = restfulRoot.getRootBean();
        for (ParseRestfulSyntaxTreeUtil.ClassBean classBean : rootBean.getClassList()) {
            result.add(genDtoFile(restfulRoot, classBean));
        }
    }

    private String commentDocToStringWrapParam(ParseRestfulSyntaxTreeUtil.CommentBean comment) {
        String value;
        if (comment == null || StrUtil.isBlank(comment.getCommentValue())) {
            value = "";
        } else {
            value = comment.getCommentValue().trim();
        }
        return JSONUtil.quote(value, true);
    }

    private String getRestGeneratePath(ParseRestfulSyntaxTreeUtil.RestfulRootIface restfulRoot, String fileName) {
        String namespace = restfulRoot.getRootBean().getJavaNameSpace();
        String moduleName = null;
        if (!CollUtil.isEmpty(restfulDslProperties.getIncludeModules())) {
            //多模块
            for (String s : restfulDslProperties.getIncludeModules()) {
                if (namespace.startsWith(restfulDslProperties.getBasePackage() + "." + s.replaceAll("[-]", "_") + ".")) {
                    moduleName = s;
                    break;
                }
            }
//            moduleName = namespace.substring(namespace.lastIndexOf(".") + 1);
        }
        StringJoiner javaFilePath = new StringJoiner(File.separator);
        if (moduleName == null) {
            javaFilePath.add(RestfulDslConstants.PROJECT_ROOT_PATH).add("src").add("main").add("java").add(StrUtil.replace(namespace, ".", File.separator)).add(fileName + ".java");
        } else {
            javaFilePath.add(RestfulDslConstants.PROJECT_ROOT_PATH).add(moduleName).add("src").add("main").add("java").add(StrUtil.replace(namespace, ".", File.separator)).add(fileName + ".java");
        }
        return javaFilePath.toString();
    }
}
