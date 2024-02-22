package com.github.alphafoxz.restful_dsl.starter.service.gen.code;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.alphafoxz.restful_dsl.starter.RestfulDslConstants;
import com.github.alphafoxz.restful_dsl.starter.configuration.RestfulDslProperties;
import com.github.alphafoxz.restful_dsl.starter.toolkit.ParseRestfulSyntaxTreeUtil;
import com.github.alphafoxz.restful_dsl.starter.toolkit.RestfulTokenDefine;
import jakarta.annotation.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

@Service
public class RestfulDslGenJavaMockService implements RestfulCodeGenerator {
    private static final String TAB = "    ";
    @Resource
    private RestfulDslProperties restfulDslProperties;

    @Override
    public Set<CodeFile> genCodeFileSet(@NonNull ParseRestfulSyntaxTreeUtil.RestfulRootBean restfulRoot, String targetDir) {
        Set<CodeFile> result = CollUtil.newHashSet();
        generateMockService(restfulRoot, result);
        for (ParseRestfulSyntaxTreeUtil.RestfulIncludeBean includeBean : restfulRoot.getIncludeBeanSet()) {
            generateMockService(includeBean, result);
        }
        return result;
    }

    private void generateMockService(ParseRestfulSyntaxTreeUtil.RestfulRootIface restfulRoot, Set<CodeFile> result) {
        ParseRestfulSyntaxTreeUtil.RootBean rootBean = restfulRoot.getRootBean();
        for (ParseRestfulSyntaxTreeUtil.InterfaceBean interfaceBean : rootBean.getInterfaceList()) {
            result.add(genMockFile(restfulRoot, interfaceBean));
        }
    }

    private CodeFile genMockFile(ParseRestfulSyntaxTreeUtil.RestfulRootIface restfulRoot, ParseRestfulSyntaxTreeUtil.InterfaceBean interfaceBean) {
        CodeFile codeFile = new CodeFile();
        StringJoiner code = new StringJoiner("\n");
        StringJoiner innerCode = new StringJoiner("\n");
        Set<String> importNames = new HashSet<>();
        importNames.add("org.springframework.stereotype.Service");
        importNames.add(restfulRoot.getRootBean().getJavaNameSpace() + "." + interfaceBean.getInterfaceName());
        for (ParseRestfulSyntaxTreeUtil.InterfaceBean.InterfaceFunctionBean functionBean : interfaceBean.getInterfaceFunctionList()) {
            innerCode.add(genInterfaceImplCode(functionBean, importNames));
        }
        code.add("package " + restfulRoot.getRootBean().getJavaNameSpace().replaceAll(".apis$", ".mock;"));
        code.add("");
        for (String importName : importNames) {
            code.add("import " + importName + ";");
        }
        code.add("");
        code.add("@Service");
        code.add("public class " + interfaceBean.getInterfaceName() + "Impl implements " + interfaceBean.getInterfaceName() + "{");
        code.add(innerCode.toString());
        code.add("}");
        codeFile.setContent(code.toString());
        codeFile.setPath(getRestGeneratePath(restfulRoot, interfaceBean.getInterfaceName()));
        codeFile.setTemplatePath(restfulRoot.getFilePath());
        codeFile.setFileName(interfaceBean.getInterfaceName() + "Impl.java");
        return codeFile;
    }

    private String genInterfaceImplCode(ParseRestfulSyntaxTreeUtil.InterfaceBean.InterfaceFunctionBean functionBean, Set<String> importNames) {
        StringJoiner code = new StringJoiner("\n");
        StringJoiner paramCode = new StringJoiner(", ");
        importNames.add("org.springframework.http.ResponseEntity");
        for (ParseRestfulSyntaxTreeUtil.ParamBean param : functionBean.getParamList()) {
            String paramAnno = "";
            if (!param.isRequired()) {
                paramAnno += "@Nullable ";
            }
            requireImport(param.getParamType(), functionBean.getImportTypeName(), importNames);
            paramCode.add(paramAnno + param.getParamType().javaString() + " " + param.getParamName());
        }
        code.add(TAB + "@Override");
        code.add(TAB + "public ResponseEntity<" + functionBean.getReturnType().javaString() + "> " + functionBean.getFunctionName() + "(" + paramCode + ") {");
        ParseRestfulSyntaxTreeUtil.TypeBean returnType = functionBean.getReturnType();
        if (RestfulTokenDefine.CONTAIN_LIST.equals(returnType.getToken())) {
            importNames.add("java.util.List");
            importNames.add("java.util.ArrayList");
            code.add(TAB + TAB + "List _r = new ArrayList<>();");
            code.add(TAB + TAB + "_r.add(" + randomValue(returnType.getT1()) + ");");
            code.add(TAB + TAB + "return ResponseEntity.ok(_r);");
        } else if (RestfulTokenDefine.CONTAIN_SET.equals(returnType.getToken())) {
            importNames.add("java.util.Set");
            importNames.add("java.util.HashSet");
            code.add(TAB + TAB + "Set _r = new HashSet<>();");
            code.add(TAB + TAB + "_r.add(" + randomValue(returnType.getT1()) + ");");
            code.add(TAB + TAB + "return ResponseEntity.ok(_r);");
        } else if (RestfulTokenDefine.CONTAIN_MAP.equals(returnType.getToken())) {
            importNames.add("java.util.Map");
            importNames.add("java.util.HashMap");
            code.add(TAB + TAB + "Map _r = new HashMap<>();");
            code.add(TAB + TAB + "_r.put(" + randomValue(returnType.getT1()) + ", " + randomValue(returnType.getT2()) + ");");
            code.add(TAB + TAB + "return ResponseEntity.ok(_r);");
        } else if (returnType.isIntype()) {
            if (RestfulTokenDefine.REF_ENUM.equals(returnType.getToken())) {
                importNames.add("cn.hutool.core.util.RandomUtil");
                requireImport(returnType.getT1(), functionBean.getImportTypeName(), importNames);
                code.add(TAB + TAB + "return ResponseEntity.ok(RandomUtil.randomEle(" + returnType.getT1().javaString() + ".values()).getValue());");
            } else {
                code.add(TAB + TAB + "return ResponseEntity.ok(" + randomValue(returnType) + ");");
            }
        } else if (RestfulTokenDefine.VOID.equals(returnType.getToken())) {
            code.add(TAB + TAB + "return ResponseEntity.ok();");
        } else {
            requireImport(returnType, functionBean.getImportTypeName(), importNames);
            code.add(TAB + TAB + returnType.javaString() + " _r = new " + returnType.javaString() + "();");
            code.add(TAB + TAB + "return ResponseEntity.ok(_r);");
        }
        code.add(TAB + "}");
        return code.toString();
    }

    private void requireImport(ParseRestfulSyntaxTreeUtil.TypeBean typeBean, Set<String> allImportNames, Set<String> importNames) {
        for (String packageName : allImportNames) {
            if (packageName.endsWith("." + typeBean.javaString())) {
                importNames.add(packageName);
            }
        }
    }

    private String randomValue(ParseRestfulSyntaxTreeUtil.TypeBean typeBean) {
        if (typeBean.isIntype()) {
            switch (typeBean.getToken()) {
                case RestfulTokenDefine.Intypes.BOOLEAN:
                    return TAB + TAB + "return ResponseEntity.ok(true);";
                case RestfulTokenDefine.Intypes.BYTE:
                    return "'b'";
                case RestfulTokenDefine.Intypes.BINARY:
                    return "null";
                case RestfulTokenDefine.Intypes.STRING:
                    return TAB + TAB + "return ResponseEntity.ok(\"string\");";
                case RestfulTokenDefine.Intypes.I16:
                case RestfulTokenDefine.Intypes.I32:
                case RestfulTokenDefine.Intypes.I64:
                    return "0";
                case RestfulTokenDefine.Intypes.DOUBLE:
                    return "0.0";
                default:
                    return "null";
            }
        }
        if (RestfulTokenDefine.REF_ENUM.equals(typeBean.getToken())) {
            return "RandomUtil.randomEle(RestfulDslServerLanguageTypeEnum.values()).getValue()";
        }
        return "null";
    }

    private String getRestGeneratePath(ParseRestfulSyntaxTreeUtil.RestfulRootIface restfulRoot, String fileName) {
        String namespace = restfulRoot.getRootBean().getJavaNameSpace().replaceAll("\\.apis$", ".mock");
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
            javaFilePath.add(RestfulDslConstants.PROJECT_ROOT_PATH).add("src").add("main").add("java").add(StrUtil.replace(namespace, ".", File.separator)).add(fileName + "Impl.java");
        } else {
            javaFilePath.add(RestfulDslConstants.PROJECT_ROOT_PATH).add(moduleName).add("src").add("main").add("java").add(StrUtil.replace(namespace, ".", File.separator)).add(fileName + "Impl.java");
        }
        return javaFilePath.toString();
    }
}
