package com.github.alphafoxz.spring_boot_starter_restful_dsl.service.gen.code;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.exception.RestfulDslException;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.toolkit.ParseRestfulSyntaxTreeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

@Service
public class RestfulDslGenRustClient implements RestfulCodeGenerator {
    private static final String TAB = "    ";
    final String GET_MAPPING = "GetMapping";
    final String POST_MAPPING = "PostMapping";
    final String DELETE_MAPPING = "DeleteMapping";
    final String PUT_MAPPING = "PutMapping";
    final String PATCH_MAPPING = "PatchMapping";

    @Override
    public Set<CodeFile> genCodeFileSet(@NonNull ParseRestfulSyntaxTreeUtil.RestfulRootBean restfulRoot, String targetDir) {
        Set<CodeFile> result = CollUtil.newHashSet();
        result.add(genRustFile(restfulRoot, targetDir));
        for (ParseRestfulSyntaxTreeUtil.RestfulIncludeBean includeBean : restfulRoot.getIncludeBeanSet()) {
            result.add(genRustFile(includeBean, targetDir));
        }
        return result;
    }

    public CodeFile genRustFile(@NonNull ParseRestfulSyntaxTreeUtil.RestfulRootIface restfulRoot, String genDir) {
        final String docFormat = "/// {}";
        final String innerDocFormat = TAB + "/// {}";
        CodeFile codeFile = new CodeFile();
        String prefix = genDir + "/" + StrUtil.replace(restfulRoot.getRootBean().getTsNameSpace(), ".", "/");
        codeFile.setPath(prefix + "/" + StrUtil.toUnderlineCase(restfulRoot.getFileName()) + ".rs");
        codeFile.setFileName(restfulRoot.getFileName());
        ParseRestfulSyntaxTreeUtil.RootBean rootBean = restfulRoot.getRootBean();
        StringJoiner code = new StringJoiner("\n");
        if (CollUtil.isNotEmpty(rootBean.getInterfaceList())) {
            for (ParseRestfulSyntaxTreeUtil.InterfaceBean interfaceBean : rootBean.getInterfaceList()) {
                code.add(genApiCode(interfaceBean, rootBean));
            }
        }
        if (CollUtil.isNotEmpty(rootBean.getClassList()) || CollUtil.isNotEmpty(rootBean.getEnumList())) {
            for (ParseRestfulSyntaxTreeUtil.IncludeBean includeBean : rootBean.getIncludeList()) {
                String includeValue = includeBean.getIncludeValue();
                String includeName = StrUtil.toUnderlineCase(includeValue.substring(includeValue.lastIndexOf("/") + 1, includeValue.lastIndexOf(".")));
                includeValue = includeValue.substring(0, includeValue.lastIndexOf("/"))
                        .replaceAll("\\.\\./", "super::super::")
                        .replaceAll("\\./", "super::")
                        .replaceAll("\\.", "super")
                        .replaceAll("/", "::");
                code.add("use " + StrUtil.toUnderlineCase(includeValue) + "::" + includeName + ";");
            }
        }
        for (ParseRestfulSyntaxTreeUtil.ClassBean classBean : rootBean.getClassList()) {
            for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : classBean.getCommentList()) {
                code.add("// " + commentBean.getCommentValue());
            }
            if (classBean.getDoc() != null) {
                code.add(StrUtil.format(docFormat, classBean.getDoc().getCommentValue()));
            }
            code.add("#[derive(std::fmt::Debug, serde::Deserialize, serde::Serialize)]");
            code.add("#[serde(rename_all = \"camelCase\")]");
            code.add("pub struct " + classBean.getClassName() + " {");
            for (ParseRestfulSyntaxTreeUtil.ClassBean.ClassFieldBean fieldBean : classBean.getClassFieldList()) {
                for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : fieldBean.getCommentList()) {
                    code.add(TAB + "// " + commentBean.getCommentValue());
                }
                if (fieldBean.getDoc() != null) {
                    code.add(StrUtil.format(innerDocFormat, fieldBean.getDoc().getCommentValue()));
                }
                String fieldRustString = fieldBean.getType().rustString();
                if (ParseRestfulSyntaxTreeUtil.Modifier.OPTIONAL.equals(fieldBean.getModifier())) {
                    fieldRustString = "Option<" + fieldRustString + ">";
                }
                code.add(TAB + "pub " + StrUtil.toUnderlineCase(fieldBean.getFieldName()) + ": " + fieldRustString + ",");
            }
            code.add("}");
            code.add("impl std::convert::Into<" + classBean.getClassName() + "> for String {");
            code.add(TAB + "fn into(self) -> " + classBean.getClassName() + " {");
            code.add(TAB + TAB + "serde_json::from_str(self.as_str()).unwrap()");
            code.add(TAB + "}");
            code.add("}");
        }
        for (ParseRestfulSyntaxTreeUtil.EnumBean enumBean : rootBean.getEnumList()) {
            for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : enumBean.getCommentList()) {
                code.add("// " + commentBean.getCommentValue());
            }
            if (enumBean.getDoc() != null) {
                code.add(StrUtil.format(docFormat, enumBean.getDoc().getCommentValue()));
            }
            code.add("#[derive(");
            code.add(TAB + "std::fmt::Debug, std::cmp::PartialEq, serde_repr::Deserialize_repr, serde_repr::Serialize_repr,");
            code.add(")]");
            code.add("#[repr(i32)]");
            code.add("pub enum " + enumBean.getEnumName() + " {");
            for (ParseRestfulSyntaxTreeUtil.EnumBean.EnumInstance enumInstance : enumBean.getEnumInstance()) {
                for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : enumInstance.getCommentList()) {
                    code.add(TAB + "// " + commentBean.getCommentValue());
                }
                if (enumInstance.getDoc() != null) {
                    code.add(StrUtil.format(innerDocFormat, enumInstance.getDoc().getCommentValue()));
                }
                code.add(TAB + StrUtil.upperFirst(StrUtil.toCamelCase(enumInstance.getInstanceName())) + " = " + enumInstance.getInstanceConstant() + ",");
            }
            code.add("}");
        }
        code.add("");
        codeFile.setContent(code.toString());
        return codeFile;
    }

    private String genApiCode(ParseRestfulSyntaxTreeUtil.InterfaceBean interfaceBean, ParseRestfulSyntaxTreeUtil.RootBean rootBean) {
        StringJoiner code = new StringJoiner("\n");
        String serviceUri = "";
        List<String> requestMappingValues = interfaceBean.getAnnotationMap().get(RequestMapping.class.getSimpleName());
        if (requestMappingValues == null) {
            throw new RestfulDslException("api缺少uri映射", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (CollUtil.isNotEmpty(requestMappingValues)) {
            serviceUri = requestMappingValues.get(0);
        }
        { //处理 inerface注释
            for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : interfaceBean.getCommentList()) {
                code.add("// " + commentBean.getCommentValue());
            }
            if (interfaceBean.getDoc() != null) {
                StringJoiner apiDoc = new StringJoiner("\n///", "///", "");
                for (String s : interfaceBean.getDoc().getCommentValue().split("\n", -1)) {
                    apiDoc.add(s);
                }
                code.add(apiDoc.toString());
            }
        }
        // 生成api mod
        code.add("pub mod " + StrUtil.toUnderlineCase(interfaceBean.getInterfaceName()) + " {");
        for (ParseRestfulSyntaxTreeUtil.IncludeBean includeBean : rootBean.getIncludeList()) {
            String includeValue = includeBean.getIncludeValue();
            String includeName = StrUtil.toUnderlineCase(includeValue.substring(includeValue.lastIndexOf("/") + 1, includeValue.lastIndexOf(".")));
            includeValue = includeValue.substring(0, includeValue.lastIndexOf("/"))
                    .replaceAll("\\.\\./", "super::super::super::")
                    .replaceAll("\\./", "super::super::")
                    .replaceAll("\\.", "super::super")
                    .replaceAll("/", "::");
            code.add(TAB + "use " + StrUtil.toUnderlineCase(includeValue) + "::" + includeName + ";");
        }
        String namespace = rootBean.getRustNameSpace();
        code.add(TAB + "use crate::core::error::Error;");
//        code.add(TAB + "use reqwest::Response;");
        String importStr = "use super::super::";
        int layer = StrUtil.count(namespace, ".") + 1;
        for (int i = 0; i < layer; i++) {
            importStr += "super::";
        }
        importStr += "apis_util;";
        code.add(TAB + importStr);
        code.add("");
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
                        executeFormat = TAB + TAB + "let __res = reqwest::Client::new()\n" +
                                TAB + TAB + TAB + ".get(apis_util::get_server_uri() + \"{}\")\n" +
                                TAB + TAB + TAB + ".send()\n" +
                                TAB + TAB + TAB + ".await;";
                        break;
                    }
                    case DELETE_MAPPING: {
                        functionUri = annoEntry.getValue().get(0);
                        executeFormat = TAB + TAB + "let __res = reqwest::Client::new()\n" +
                                TAB + TAB + TAB + ".delete(apis_util::get_server_uri() + \"{}\")\n" +
                                TAB + TAB + TAB + ".send()\n" +
                                TAB + TAB + TAB + ".await;";
                        break;
                    }
                    case POST_MAPPING: {
                        functionUri = annoEntry.getValue().get(0);
                        executeFormat = TAB + TAB + "let __param = serde_json::json!({});\n" +
                                TAB + TAB + "let __res = reqwest::Client::new()\n" +
                                TAB + TAB + TAB + ".post(apis_util::get_server_uri() + \"{}\")\n" +
                                TAB + TAB + TAB + ".header(\"Content-Type\", \"application/json\")\n" +
                                TAB + TAB + TAB + ".body(__param.to_string())\n" +
                                TAB + TAB + TAB + ".send()\n" +
                                TAB + TAB + TAB + ".await;";
                        isPost = true;
                        break;
                    }
                    case PUT_MAPPING: {
                        functionUri = annoEntry.getValue().get(0);
                        executeFormat = TAB + TAB + "let __param = serde_json::json!({});\n" +
                                TAB + TAB + "let __res = reqwest::Client::new()\n" +
                                TAB + TAB + TAB + ".put(apis_util::get_server_uri() + \"{}\")\n" +
                                TAB + TAB + TAB + ".header(\"Content-Type\", \"application/json\")\n" +
                                TAB + TAB + TAB + ".body(__param.to_string())\n" +
                                TAB + TAB + TAB + ".send()\n" +
                                TAB + TAB + TAB + ".await;";
                        isPost = true;
                        break;
                    }
                    case PATCH_MAPPING: {
                        functionUri = annoEntry.getValue().get(0);
                        executeFormat = TAB + TAB + "let __param = serde_json::json!({});\n" +
                                TAB + TAB + "let __res = reqwest::Client::new()\n" +
                                TAB + TAB + TAB + ".patch(apis_util::get_server_uri() + \"{}\")\n" +
                                TAB + TAB + TAB + ".header(\"Content-Type\", \"application/json\")\n" +
                                TAB + TAB + TAB + ".body(__param.to_string())\n" +
                                TAB + TAB + TAB + ".send()\n" +
                                TAB + TAB + TAB + ".await;";
                        isPost = true;
                        break;
                    }
                }
            }
            code.add(genApiFunctions(interfaceFunction, isPage, isPost, serviceUri, functionUri, executeFormat));
        }
        code.add("}");
        return code.toString();
    }

    private String genApiFunctions(ParseRestfulSyntaxTreeUtil.InterfaceBean.InterfaceFunctionBean interfaceFunction,
                                   boolean isPage,
                                   boolean isPost,
                                   String serviceUri,
                                   String functionUri,
                                   String executeFormat) {
        StringJoiner result = new StringJoiner("\n");
        {
            // 处理注释
            for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : interfaceFunction.getCommentList()) {
                result.add(TAB + "// " + commentBean.getCommentValue());
            }
            StringJoiner innerDoc = new StringJoiner("\n" + TAB + "///", TAB + "///", "");
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
                        String prefix = i == 0 ? "_" + param.getParamName() + ": " : TAB + TAB;
                        innerDoc.add(prefix + s);
                        i++;
                    }
                }
            }
            result.add(innerDoc.toString());
        }
        String executeParam;
        String functionHeader = TAB + "pub async fn " + StrUtil.toUnderlineCase(interfaceFunction.getFunctionName());
//        String returnTypeString = "Result<Response, Error>";
        String returnTypeString = isPage ? "_Page<" + interfaceFunction.getReturnType().rustString() + ">" : interfaceFunction.getReturnType().rustString();
        returnTypeString = "Result<" + returnTypeString + ", Error>";
        Set<String> pathVarSet = CollUtil.newHashSet();
        for (String match : ReUtil.findAllGroup0("\\{\\s*\\w+\\s*}", functionUri)) {
            String pathVar = match.substring(1, match.length() - 1).trim();
            functionUri = StrUtil.replaceFirst(functionUri, match, "${encodeURI(_" + pathVar + ".toString())}");
            pathVarSet.add(pathVar);
        }
        if (isPost && interfaceFunction.getParamList().size() == 1 && !interfaceFunction.getParamList().get(0).getParamType().isIntype()) {
            // 只有单个参数
            executeParam = "_" + StrUtil.toUnderlineCase(interfaceFunction.getParamList().get(0).getParamName());
        } else {
            StringJoiner executeParamJoiner;
            if (isPost) {
                executeParamJoiner = new StringJoiner(", ", "{ ", " }");
                executeParamJoiner.setEmptyValue("");
            } else {
                executeParamJoiner = new StringJoiner(" + \"&", "?", " + \"");
                executeParamJoiner.setEmptyValue("");
            }
            for (ParseRestfulSyntaxTreeUtil.ParamBean param : interfaceFunction.getParamList()) {
                if (pathVarSet.contains(param.getParamName())) {
                    continue;
                }
                if (isPost) {
                    executeParamJoiner.add("\"" + param.getParamName() + "\": _" + StrUtil.toUnderlineCase(param.getParamName()));
                    continue;
                }
//                if (param.getParamType().isIntype() || param.getParamType().isCollection()) {
                executeParamJoiner.add(param.getParamName() + "=\" + url::form_urlencoded::byte_serialize(_" + StrUtil.toUnderlineCase(param.getParamName()) + ".as_bytes()).collect::<String>().as_str()");
//                } else {
//                    executeParamJoiner.add(param.getParamName() + "=" + "${encodeURI(_JSON().stringify(_" + StrUtil.toUnderlineCase(param.getParamName()) + "))}");
//                }
            }
            executeParam = executeParamJoiner.toString();
        }
        StringJoiner paramStringJoiner = new StringJoiner(", ", "(", ")");
        for (ParseRestfulSyntaxTreeUtil.ParamBean param : interfaceFunction.getParamList()) {
            String otherType = "";
            if (ParseRestfulSyntaxTreeUtil.Modifier.OPTIONAL.equals(param.getModifier())) {
                otherType += " | undefined";
            }
            paramStringJoiner.add("_" + StrUtil.toUnderlineCase(param.getParamName()) + ": " + param.getParamType().rustString() + otherType);
        }

        StringJoiner config = new StringJoiner("", "{", "}");
        if (interfaceFunction.getReturnType().isCollection() && ParseRestfulSyntaxTreeUtil.Intypes.BYTE.equals(interfaceFunction.getReturnType().getT1().getToken())) {
            // 下载二进制文件
            config.add("\n" + TAB + TAB + TAB + "responseType: 'blob',");
        }
        if (config.length() > 2) {
            config.add("\n" + TAB + TAB);
        }
        result.add(functionHeader + paramStringJoiner + " -> " + returnTypeString + " {");
        if (isPost) {
            result.add(StrUtil.format(executeFormat, executeParam, serviceUri + functionUri, config));
        } else {
            result.add(StrUtil.format(executeFormat, serviceUri + functionUri + executeParam, config));
        }
        result.add(TAB + TAB + "if __res.is_err() {");
        result.add(TAB + TAB + TAB + "return Err(\"请求失败\".into());");
        result.add(TAB + TAB + "}");
        result.add(TAB + TAB + "Ok(__res.unwrap().json().await.unwrap())");
        result.add(TAB + "}");
        return result.toString();
    }
}
