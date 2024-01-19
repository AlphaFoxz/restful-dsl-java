package com.github.alphafoxz.spring_boot_starter_restful_dsl.service.gen.code;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.exception.RestfulDslException;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.toolkit.ParseRestfulSyntaxTreeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

@Slf4j
@Service
public class RestfulDslGenTsClient implements RestfulCodeGenerator {
    private static final String TAB = "  ";
    final String GET_MAPPING = "GetMapping";
    final String POST_MAPPING = "PostMapping";
    final String DELETE_MAPPING = "DeleteMapping";
    final String PUT_MAPPING = "PutMapping";
    final String PATCH_MAPPING = "PatchMapping";

    @Override
    public Set<CodeFile> genCodeFileSet(@NonNull ParseRestfulSyntaxTreeUtil.RestfulRootBean restfulRoot, String targetDir) {
        Set<CodeFile> result = CollUtil.newHashSet();
        result.add(genTsFile(restfulRoot, targetDir));
        for (ParseRestfulSyntaxTreeUtil.RestfulIncludeBean includeBean : restfulRoot.getIncludeBeanSet()) {
            result.add(genTsFile(includeBean, targetDir));
        }
        return result;
    }

    private CodeFile genTsFile(@NonNull ParseRestfulSyntaxTreeUtil.RestfulRootIface restfulRoot, @NonNull String genDir) {
        final String docFormat = "/**\n" +
                " * {}\n" +
                " */";
        final String innerDocFormat = TAB + "/**\n" +
                TAB + " * {}\n" +
                TAB + " */";
        CodeFile codeFile = new CodeFile();
        String prefix = genDir + "/" + StrUtil.replace(restfulRoot.getRootBean().getTsNameSpace(), ".", "/");
        codeFile.setPath(prefix + "/" + restfulRoot.getFileName() + ".ts");
        codeFile.setFileName(restfulRoot.getFileName());
        ParseRestfulSyntaxTreeUtil.RootBean rootBean = restfulRoot.getRootBean();
        String namespace = rootBean.getTsNameSpace();
        StringJoiner code = new StringJoiner("\n");
        for (ParseRestfulSyntaxTreeUtil.IncludeBean includeBean : rootBean.getIncludeList()) {
            String includeValue = includeBean.getIncludeValue();
            String includeName = includeValue.substring(includeValue.lastIndexOf("/") + 1, includeValue.lastIndexOf("."));
            includeValue = includeValue.substring(0, includeValue.lastIndexOf("."));
            code.add("import * as " + includeName + " from '" + includeValue + "'");
        }
        if (namespace.contains("apis")) {
            String importStr = "import {\n";
            importStr += TAB + "requireHttpUtil as _http,\n";
            importStr += TAB + "requireJSON as _JSON,\n";
            importStr += TAB + "type Page as _Page,\n";
            importStr += TAB + "type HttpResult as _HttpResult\n";
            importStr += "} from '";
            int layer = StrUtil.count(namespace, ".") + 1;
            for (int i = 0; i < layer; i++) {
                importStr += "../";
            }
            importStr += "apis-util'";
            code.add(importStr);
            code.add("");
            for (ParseRestfulSyntaxTreeUtil.InterfaceBean interfaceBean : rootBean.getInterfaceList()) {
                code.add(genApiCode(interfaceBean));
            }
        }
        for (ParseRestfulSyntaxTreeUtil.ClassBean classBean : rootBean.getClassList()) {
            for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : classBean.getCommentList()) {
                code.add("// " + commentBean.getCommentValue());
            }
            if (classBean.getDoc() != null) {
                code.add(StrUtil.format(docFormat, classBean.getDoc().getCommentValue()));
            }
            code.add("export type " + classBean.getClassName() + " = {");
            for (ParseRestfulSyntaxTreeUtil.ClassBean.ClassFieldBean fieldBean : classBean.getClassFieldList()) {
                for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : fieldBean.getCommentList()) {
                    code.add(TAB + "// " + commentBean.getCommentValue());
                }
                if (fieldBean.getDoc() != null) {
                    code.add(StrUtil.format(innerDocFormat, fieldBean.getDoc().getCommentValue()));
                }
                String fieldTsString = fieldBean.getType().tsString() + (ParseRestfulSyntaxTreeUtil.Modifier.OPTIONAL.equals(fieldBean.getModifier()) ? " | undefined" : "");
                code.add(TAB + fieldBean.getFieldName() + ": " + fieldTsString);
            }
            code.add("}");
        }
        for (ParseRestfulSyntaxTreeUtil.EnumBean enumBean : rootBean.getEnumList()) {
            for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : enumBean.getCommentList()) {
                code.add("// " + commentBean.getCommentValue());
            }
            if (enumBean.getDoc() != null) {
                code.add(StrUtil.format(docFormat, enumBean.getDoc().getCommentValue()));
            }
            code.add("export enum " + enumBean.getEnumName() + " {");
            for (ParseRestfulSyntaxTreeUtil.EnumBean.EnumInstance enumInstance : enumBean.getEnumInstance()) {
                for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : enumInstance.getCommentList()) {
                    code.add(TAB + "// " + commentBean.getCommentValue());
                }
                if (enumInstance.getDoc() != null) {
                    code.add(StrUtil.format(innerDocFormat, enumInstance.getDoc().getCommentValue()));
                }
                code.add(TAB + enumInstance.getInstanceName() + " = " + enumInstance.getInstanceConstant() + ",");
            }
            code.add("}");
        }
        code.add("");
        codeFile.setContent(code.toString());
        return codeFile;
    }

    /**
     * 创建api代码
     * 由于post方式请求体为json对象，但是当参数很简单或者复用的可能性极低时，为这种参数额外建模纯属于无效工作量
     * 所以在请求时统一放进一个对象，后端生成的代码做对应的解析处理
     */
    private String genApiCode(ParseRestfulSyntaxTreeUtil.InterfaceBean interfaceBean) {
        StringJoiner code = new StringJoiner("\n");
        String serviceUri = "";
        List<String> requestMappingValues = interfaceBean.getAnnotationMap().get(RequestMapping.class.getSimpleName());
        if (requestMappingValues == null) {
            throw new RestfulDslException("api缺少uri映射", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (CollUtil.isNotEmpty(requestMappingValues)) {
            serviceUri = requestMappingValues.get(0);
        }
        // 生成函数与函数重载
        code.add("namespace f_" + interfaceBean.getInterfaceName() + " {");
        for (ParseRestfulSyntaxTreeUtil.InterfaceBean.InterfaceFunctionBean interfaceFunction : interfaceBean.getInterfaceFunctionList()) {
            boolean isPost = false;
            boolean isFormData = false;
            String functionUri = "";
            String executeFormat = "";

            boolean isPage = false;
            // 处理@注解
            for (Map.Entry<String, List<String>> annoEntry : interfaceFunction.getAnnotationMap().entrySet()) {
                switch (annoEntry.getKey()) {
                    case "Page": {
                        isPage = true;
                        break;
                    }
                    case "FormData": {
                        isFormData = true;
                        break;
                    }
                    case "RequestMapping": {
                        throw new RestfulDslException("@uri不允许注解在具体方法上，必须指定一个特定的http方法，请使用@postUri或@getUri", HttpStatus.INTERNAL_SERVER_ERROR);
//                        break;
                    }
                    case GET_MAPPING: {
                        functionUri = annoEntry.getValue().get(0);
                        executeFormat = TAB + TAB + "return (await _http()).get(`{}`, {})";
                        break;
                    }
                    case DELETE_MAPPING: {
                        functionUri = annoEntry.getValue().get(0);
                        executeFormat = TAB + TAB + "return (await _http()).delete(`{}`, {})";
                        break;
                    }
                    case POST_MAPPING: {
                        functionUri = annoEntry.getValue().get(0);
                        executeFormat = TAB + TAB + "return (await _http()).post(`{}`, {}, {})";
                        isPost = true;
                        break;
                    }
                    case PUT_MAPPING: {
                        functionUri = annoEntry.getValue().get(0);
                        executeFormat = TAB + TAB + "return (await _http()).put(`{}`, {}, {})";
                        isPost = true;
                        break;
                    }
                    case PATCH_MAPPING: {
                        functionUri = annoEntry.getValue().get(0);
                        executeFormat = TAB + TAB + "return (await _http()).patch(`{}`, {}, {})";
                        isPost = true;
                        break;
                    }
                }
            }
            if (isFormData) { // 需要函数重载
                code.add(genFormDataFunctionCode(interfaceFunction, isPage, serviceUri, functionUri, executeFormat));
            } else {
                code.add(genJsonFunctionCode(interfaceFunction, isPage, isPost, serviceUri, functionUri, executeFormat));
            }

        }
        code.add("}");

        // 封装api
        for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : interfaceBean.getCommentList()) {
            code.add("// " + commentBean.getCommentValue());
        }
        if (interfaceBean.getDoc() != null) {
            StringJoiner apiDoc = new StringJoiner("\n * ", "/**\n * ", "\n */");
            for (String s : interfaceBean.getDoc().getCommentValue().split("\n", -1)) {
                apiDoc.add(s);
            }
            code.add(apiDoc.toString());
        }
        code.add("export const " + interfaceBean.getInterfaceName() + " = {");
        for (ParseRestfulSyntaxTreeUtil.InterfaceBean.InterfaceFunctionBean interfaceFunction : interfaceBean.getInterfaceFunctionList()) {
            {
                // 处理注释
                for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : interfaceFunction.getCommentList()) {
                    code.add(TAB + "// " + commentBean.getCommentValue());
                }
                StringJoiner innerDoc = new StringJoiner("\n" + TAB + " * ", TAB + "/**\n" + TAB + " * ", "\n" + TAB + " */");
                innerDoc.setEmptyValue("");
                if (interfaceFunction.getDoc() != null) {
                    for (String s : interfaceFunction.getDoc().getCommentValue().split("\n", -1)) {
                        innerDoc.add(s);
                    }
                }
                for (ParseRestfulSyntaxTreeUtil.ParamBean param : interfaceFunction.getParamList()) {
                    if (param.getDoc() != null) {
                        int i = 0;
                        for (String s : param.getDoc().getCommentValue().split("\n", -1)) {
                            String prefix = i == 0 ? "@param p_" + param.getParamName() + " " : TAB + TAB;
                            innerDoc.add(prefix + s);
                            i++;
                        }
                    }
                }
                code.add(innerDoc.toString());
            }
            String functionName = interfaceFunction.getFunctionName();
            code.add(TAB + functionName + ": f_" + interfaceBean.getInterfaceName() + "." + functionName + ",");
        }
        code.add("}");
        return code.toString();
    }

    /**
     * 生成JSON请求函数
     */
    private String genJsonFunctionCode(ParseRestfulSyntaxTreeUtil.InterfaceBean.InterfaceFunctionBean interfaceFunction,
                                       boolean isPage,
                                       boolean isPost,
                                       String serviceUri,
                                       String functionUri,
                                       String executeFormat) {
        StringJoiner result = new StringJoiner("\n");
        String executeParam = "";
        String functionHeader = TAB + "export async function " + interfaceFunction.getFunctionName();
        String returnTypeString = isPage ? "_Page<" + interfaceFunction.getReturnType().tsString() + ">" : interfaceFunction.getReturnType().tsString();
        returnTypeString = "_HttpResult<" + returnTypeString + ">";
        Set<String> pathVarSet = CollUtil.newHashSet();
        for (String match : ReUtil.findAllGroup0("\\{\\s*\\w+\\s*}", functionUri)) {
            String pathVar = match.substring(1, match.length() - 1).trim();
            functionUri = StrUtil.replaceFirst(functionUri, match, "${encodeURI(p_" + pathVar + ".toString())}");
            pathVarSet.add(pathVar);
        }
        if (isPost && interfaceFunction.getParamList().size() == 1 && !interfaceFunction.getParamList().get(0).getParamType().isIntype()) {
            // 只有单个参数
            executeParam = "p_" + interfaceFunction.getParamList().get(0).getParamName();
        } else {
            StringJoiner executeParamJoiner;
            if (isPost) {
                executeParamJoiner = new StringJoiner(", ", "{ ", " }");
                executeParamJoiner.setEmptyValue("");
            } else {
                executeParamJoiner = new StringJoiner("&", "?", "");
                executeParamJoiner.setEmptyValue("");
            }
            for (ParseRestfulSyntaxTreeUtil.ParamBean param : interfaceFunction.getParamList()) {
                if (pathVarSet.contains(param.getParamName())) {
                    continue;
                }
                if (isPost) {
                    executeParamJoiner.add(param.getParamName() + ": p_" + param.getParamName());
                    continue;
                }
                if (param.getParamType().isIntype() || param.getParamType().isCollection()) {
                    executeParamJoiner.add(param.getParamName() + "=" + "${encodeURI(p_" + param.getParamName() + ".toString())}");
                } else {
                    executeParamJoiner.add(param.getParamName() + "=" + "${encodeURI(_JSON().stringify(p_" + param.getParamName() + "))}");
                }
            }
            executeParam = executeParamJoiner.toString();
        }
        StringJoiner paramStringJoiner = new StringJoiner(", ", "(", ")");
        for (ParseRestfulSyntaxTreeUtil.ParamBean param : interfaceFunction.getParamList()) {
            String otherType = "";
            if (ParseRestfulSyntaxTreeUtil.Modifier.OPTIONAL.equals(param.getModifier())) {
                otherType += " | undefined";
            }
            paramStringJoiner.add("p_" + param.getParamName() + ": " + param.getParamType().tsString() + otherType);
        }

        StringJoiner config = new StringJoiner("", "{", "}");
        if (interfaceFunction.getReturnType().isCollection() && ParseRestfulSyntaxTreeUtil.Intypes.BYTE.equals(interfaceFunction.getReturnType().getT1().getToken())) {
            // 下载二进制文件
            config.add("\n" + TAB + TAB + TAB + "responseType: 'blob',");
        }
        if (config.length() > 2) {
            config.add("\n" + TAB + TAB);
        }
        result.add(functionHeader + paramStringJoiner + ": " + returnTypeString + " {");
        if (isPost) {
            result.add(StrUtil.format(executeFormat, serviceUri + functionUri, executeParam, config));
        } else {
            result.add(StrUtil.format(executeFormat, serviceUri + functionUri + executeParam, config));
        }
        result.add(TAB + "}");
        return result.toString();
    }

    /**
     * 生成formData请求函数
     */
    private String genFormDataFunctionCode(ParseRestfulSyntaxTreeUtil.InterfaceBean.InterfaceFunctionBean interfaceFunction,
                                           boolean isPage,
                                           String serviceUri,
                                           String functionUri,
                                           String executeFormat) {
        StringJoiner result = new StringJoiner("\n");
        String returnTypeString = isPage ? "_Page<" + interfaceFunction.getReturnType().tsString() + ">" : interfaceFunction.getReturnType().tsString();
        returnTypeString = "_HttpResult<" + returnTypeString + ">";
        { // 针对FormData进行重载
            result.add(TAB + "/**");
            result.add(TAB + " * @param p_data 表单数据");
            result.add(TAB + " */");
            result.add(TAB + "export async function " + interfaceFunction.getFunctionName() + "(p_data: FormData): " + returnTypeString);
        }
        { // 针对restful设计字段进行重载
            StringJoiner paramCode = new StringJoiner(", ");
            for (ParseRestfulSyntaxTreeUtil.ParamBean paramBean : interfaceFunction.getParamList()) {
                String otherType = ParseRestfulSyntaxTreeUtil.Modifier.OPTIONAL.equals(paramBean.getParamType().getModifier()) ? " | undefined" : "";
                paramCode.add("p_" + paramBean.getParamName() + ": " + paramBean.getParamType().tsString() + otherType);
            }
            result.add(TAB + "export async function " + interfaceFunction.getFunctionName() + "(" + paramCode + "): " + returnTypeString);
        }
        { // 实现函数
            String firstParamName;
            if (interfaceFunction.getParamList().isEmpty()) {
                firstParamName = "p_data";
            } else {
                firstParamName = "p_" + interfaceFunction.getParamList().get(0).getParamName();
            }
            StringJoiner paramCode = new StringJoiner(", ").setEmptyValue("p_data?: FormData");
            for (int i = 0; i < interfaceFunction.getParamList().size(); i++) {
                ParseRestfulSyntaxTreeUtil.ParamBean paramBean = interfaceFunction.getParamList().get(i);
                if (i == 0) {
                    String otherType = ParseRestfulSyntaxTreeUtil.Modifier.OPTIONAL.equals(paramBean.getParamType().getModifier()) ? " | undefined" : "";
                    otherType += " | FormData";
                    paramCode.add("p_" + paramBean.getParamName() + ": " + paramBean.getParamType().tsString() + otherType);
                } else {
                    paramCode.add("p_" + paramBean.getParamName() + "?: " + paramBean.getParamType().tsString());
                }
            }
            result.add(TAB + "export async function " + interfaceFunction.getFunctionName() + "(" + paramCode + "): " + returnTypeString + " {");
            result.add(TAB + TAB + "let _data: FormData");
            result.add(TAB + TAB + "if (" + firstParamName + " instanceof FormData) {");
            result.add(TAB + TAB + TAB + "_data = " + firstParamName);
            result.add(TAB + TAB + "} else {");
            result.add(TAB + TAB + TAB + "_data = new FormData()");
            for (ParseRestfulSyntaxTreeUtil.ParamBean paramBean : interfaceFunction.getParamList()) {
                if (paramBean.getParamType().isCollection() && ParseRestfulSyntaxTreeUtil.Intypes.BINARY.equals(paramBean.getParamType().getT1().getToken())) {
                    result.add(TAB + TAB + TAB + "Array.from(p_" + paramBean.getParamName() + ").forEach((_e) => {");
                    result.add(TAB + TAB + TAB + TAB + "_data.append('" + paramBean.getParamName() + "', _e)");
                    result.add(TAB + TAB + TAB + "})");
                    continue;
                } else if (ParseRestfulSyntaxTreeUtil.Intypes.BINARY.equals(paramBean.getParamType().getToken())) {
                    result.add(TAB + TAB + TAB + "_data.append('" + paramBean.getParamName() + "', p_" + paramBean.getParamName() + ")");
                    continue;
                }
                result.add(TAB + TAB + TAB + "_data.append('" + paramBean.getParamName() + "', p_" + paramBean.getParamName() + "!.toString())");
            }
            result.add(TAB + TAB + "}");
            result.add(StrUtil.format(executeFormat, serviceUri + functionUri, "_data", "{\n" + TAB + TAB + TAB + "headers: { 'Content-Type': 'multipart/form-data' },\n" + TAB + TAB + "}"));
        }
        result.add(TAB + "}");
        return result.toString();
    }
}