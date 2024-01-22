package com.github.alphafoxz.spring_boot_starter_restful_dsl.service.gen;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.RestfulDslConstants;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.dtos.SdkCodeTemplateDto;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.dtos.SdkCodeTemplateRequestDto;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.dtos.SdkListResponseDto;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.dtos.SdkMapResponseDto;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.service.RestfulDslInfoService;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.service.gen.code.*;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.service.version.RestfulDslVersionStoreService;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.toolkit.ParseRestfulSyntaxTreeUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

@Slf4j
@Service
public class RestfulDslGenCodeService {
    @Resource
    private Snowflake snowflake;
    @Resource
    private RestfulDslInfoService restfulDslInfoService;
    @Resource
    private RestfulDslGenJavaServer restfulDslGenJavaServer;
    @Resource
    private RestfulDslGenTsClient restfulDslGenTsClient;
    @Resource
    private RestfulDslGenRustClient restfulDslGenRustClient;
    @Resource
    private RestfulDslGenSqlPostgre restfulDslGenSqlPostgre;
    @Resource
    private RestfulDslVersionStoreService restfulDslVersionStoreService;

    public SdkMapResponseDto previewGenerateTsApi(SdkCodeTemplateRequestDto templateDto, String genDir) {
        SdkMapResponseDto result = new SdkMapResponseDto()
                .setId(snowflake.nextId()).setTaskId(templateDto.getTaskId()).setSuccess(false);
        result.setData(MapUtil.newHashMap());
        // 检查基本SDK环境
        SdkListResponseDto checkInfo = restfulDslInfoService.checkErr();
        if (!checkInfo.getSuccess()) {
            log.error("检查thrift时发现错误 {}", checkInfo.getMessage());
            result.setMessage("检查thrift时发现错误");
            return result;
        }
        ParseRestfulSyntaxTreeUtil.RestfulRootBean restfulRoot;
        try {
            restfulRoot = parseRestfulRoot(templateDto.getData());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        genDir = StrUtil.replace(genDir, "\\", "/");
        Set<CodeFile> fileSet = restfulDslGenTsClient.genCodeFileSet(restfulRoot, genDir);
        for (CodeFile codeFile : fileSet) {
            result.getData().put(codeFile.getPath(), codeFile.getContent());
        }
        result.setSuccess(true);
        return result;
    }

    public SdkMapResponseDto previewGenerateRustApi(SdkCodeTemplateRequestDto templateDto, String genDir) {
        SdkMapResponseDto result = new SdkMapResponseDto()
                .setId(snowflake.nextId()).setTaskId(templateDto.getTaskId()).setSuccess(false);
        result.setData(MapUtil.newHashMap());
        // 检查基本SDK环境
        SdkListResponseDto checkInfo = restfulDslInfoService.checkErr();
        if (!checkInfo.getSuccess()) {
            log.error("检查thrift时发现错误 {}", checkInfo.getMessage());
            result.setMessage("检查thrift时发现错误");
            return result;
        }
        ParseRestfulSyntaxTreeUtil.RestfulRootBean restfulRoot;
        try {
            restfulRoot = parseRestfulRoot(templateDto.getData());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        genDir = StrUtil.replace(genDir, "\\", "/");
        Set<CodeFile> fileSet = restfulDslGenRustClient.genCodeFileSet(restfulRoot, genDir);
        for (CodeFile codeFile : fileSet) {
            result.getData().put(codeFile.getPath(), codeFile.getContent());
        }
        result.setSuccess(true);
        return result;
    }


    public SdkListResponseDto generateJavaApi(SdkCodeTemplateRequestDto templateDto) {
        SdkListResponseDto result = new SdkListResponseDto()
                .setId(snowflake.nextId()).setTaskId(templateDto.getTaskId()).setSuccess(false);
        // 检查基本SDK环境
        SdkListResponseDto checkInfo = restfulDslInfoService.checkErr();
        if (!checkInfo.getSuccess()) {
            log.error("检查thrift时发现错误 {}", checkInfo.getMessage());
            result.setMessage("检查thrift时发现错误");
            return result;
        }
        // 映射语法树
        ParseRestfulSyntaxTreeUtil.RestfulRootBean restfulRoot;
        try {
            restfulRoot = parseRestfulRoot(templateDto.getData());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        Set<CodeFile> codeFiles = restfulDslGenJavaServer.genCodeFileSet(restfulRoot, null);
        boolean genResult = true;
        TreeMap<String, Serializable> restfulVersionMap = restfulDslVersionStoreService.genRestfulStore().readFile();
        for (CodeFile codeFile : codeFiles) {
            genResult = codeFile.writeToLocal();
            if (!genResult) {
                break;
            }
            String hash = SecureUtil.sha256(FileUtil.readUtf8String(codeFile.getTemplatePath()).replace("\r\n", "\n"));
            restfulVersionMap.put(codeFile.getTemplatePath().replace(RestfulDslConstants.SDK_GEN_RESTFUL_TEMPLATE_PATH, ""), hash);
        }
        restfulDslVersionStoreService.genRestfulStore().writeFile(restfulVersionMap);
        result.setSuccess(genResult);
        return result;
    }

    public SdkMapResponseDto previewGenerateSql(SdkCodeTemplateRequestDto templateDto) {
        SdkMapResponseDto result = new SdkMapResponseDto()
                .setId(snowflake.nextId()).setTaskId(templateDto.getTaskId()).setSuccess(false);
        result.setData(MapUtil.newHashMap());
        // 检查基本SDK环境
        SdkListResponseDto checkInfo = restfulDslInfoService.checkErr();
        if (!checkInfo.getSuccess()) {
            log.error("检查thrift时发现错误 {}", checkInfo.getMessage());
            result.setMessage("检查thrift时发现错误");
            return result;
        }
        ParseRestfulSyntaxTreeUtil.RestfulRootBean restfulRootBean;
        try {
            restfulRootBean = parseRestfulRoot(templateDto.getData());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        Set<CodeFile> fileSet = restfulDslGenSqlPostgre.genCodeFileSet(restfulRootBean, "");
        for (CodeFile codeFile : fileSet) {
            result.getData().put(codeFile.getPath(), codeFile.getContent());
        }
        result.setSuccess(true);
        return result;
    }

    private ParseRestfulSyntaxTreeUtil.RestfulRootBean parseRestfulRoot(SdkCodeTemplateDto templateDto) {
        ParseRestfulSyntaxTreeUtil.RestfulRootBean restfulRoot;
        ParseRestfulSyntaxTreeUtil.RootBean rootBean;
        try {
            restfulRoot = ParseRestfulSyntaxTreeUtil.parseRestfulRoot(templateDto);
            rootBean = restfulRoot.getRootBean();
        } catch (Exception e) {
            log.error("解析json异常，请检查传参", e);
            throw new RuntimeException("解析json异常，请检查传参\n" + e.getMessage());
        }
        if (!rootBean.getNamespaceMap().containsKey(ParseRestfulSyntaxTreeUtil.NamespaceBean.NamespaceLangEnum.TS)) {
            log.error("语法树中缺少namespace");
            throw new RuntimeException("语法树中缺少namespace");
        }
        return restfulRoot;
    }

    private String readJavaNamespace(File thriftFile) {
        if (!StrUtil.endWithIgnoreCase(thriftFile.getName(), "thrift")) {
            log.error("{} 拓展名错误！不是一个有效的thrift文件", thriftFile.getAbsolutePath());
            return null;
        }
        List<String> content = CollUtil.newArrayList();
        FileUtil.readLines(thriftFile, StandardCharsets.UTF_8, content);
        for (String lineStr : content) {
            lineStr = lineStr.trim();
            if (lineStr.startsWith("namespace") && ReUtil.isMatch("^namespace[\\s]+java[\\s]+[^\\s]+$", lineStr)) {
                return lineStr.split("\\s")[2];
            }
        }
        log.error("thrift文件缺少namespace");
        return null;
    }

    private void fixFile(String path) {
        for (File file : FileUtil.loopFiles(path)) {
            String s = FileUtil.readUtf8String(file);
            FileUtil.writeUtf8String(StrUtil.replace(s, "@javax.annotation.Generated", "@javax.annotation.processing.Generated"), file);
        }
    }
}
