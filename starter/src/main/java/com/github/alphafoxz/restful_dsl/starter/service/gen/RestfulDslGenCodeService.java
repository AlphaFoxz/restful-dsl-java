package com.github.alphafoxz.restful_dsl.starter.service.gen;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.github.alphafoxz.restful_dsl.starter.RestfulDslConstants;
import com.github.alphafoxz.restful_dsl.starter.gen.restful.dtos.RestfulDslCodeTemplateDto;
import com.github.alphafoxz.restful_dsl.starter.gen.restful.dtos.RestfulDslCodeTemplateRequestDto;
import com.github.alphafoxz.restful_dsl.starter.gen.restful.dtos.RestfulDslListResponseDto;
import com.github.alphafoxz.restful_dsl.starter.gen.restful.dtos.RestfulDslMapResponseDto;
import com.github.alphafoxz.restful_dsl.starter.service.RestfulDslInfoService;
import com.github.alphafoxz.restful_dsl.starter.service.gen.code.*;
import com.github.alphafoxz.restful_dsl.starter.service.version.RestfulDslVersionStoreService;
import com.github.alphafoxz.restful_dsl.starter.toolkit.ParseRestfulSyntaxTreeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
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

    public RestfulDslMapResponseDto previewGenerateTsApi(RestfulDslCodeTemplateRequestDto templateDto, String genDir) {
        RestfulDslMapResponseDto result = new RestfulDslMapResponseDto()
                .setId(snowflake.nextId()).setTaskId(templateDto.getTaskId()).setSuccess(false);
        result.setData(MapUtil.newHashMap());
        // 检查基本SDK环境
        RestfulDslListResponseDto checkInfo = restfulDslInfoService.checkErr();
        if (!checkInfo.getSuccess()) {
            log.error("检查restl时发现错误 {}", checkInfo.getMessage());
            result.setMessage("检查restl时发现错误");
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

    public RestfulDslMapResponseDto previewGenerateRustApi(RestfulDslCodeTemplateRequestDto templateDto, String genDir) {
        RestfulDslMapResponseDto result = new RestfulDslMapResponseDto()
                .setId(snowflake.nextId()).setTaskId(templateDto.getTaskId()).setSuccess(false);
        result.setData(MapUtil.newHashMap());
        // 检查基本SDK环境
        RestfulDslListResponseDto checkInfo = restfulDslInfoService.checkErr();
        if (!checkInfo.getSuccess()) {
            log.error("检查restl时发现错误 {}", checkInfo.getMessage());
            result.setMessage("检查restl时发现错误");
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


    public RestfulDslListResponseDto generateJavaApi(RestfulDslCodeTemplateRequestDto templateDto) {
        RestfulDslListResponseDto result = new RestfulDslListResponseDto()
                .setId(snowflake.nextId()).setTaskId(templateDto.getTaskId()).setSuccess(false);
        // 检查基本SDK环境
        RestfulDslListResponseDto checkInfo = restfulDslInfoService.checkErr();
        if (!checkInfo.getSuccess()) {
            log.error("检查restl时发现错误 {}", checkInfo.getMessage());
            result.setMessage("检查restl时发现错误");
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

    public RestfulDslMapResponseDto previewGenerateSql(RestfulDslCodeTemplateRequestDto templateDto) {
        RestfulDslMapResponseDto result = new RestfulDslMapResponseDto()
                .setId(snowflake.nextId()).setTaskId(templateDto.getTaskId()).setSuccess(false);
        result.setData(MapUtil.newHashMap());
        // 检查基本SDK环境
        RestfulDslListResponseDto checkInfo = restfulDslInfoService.checkErr();
        if (!checkInfo.getSuccess()) {
            log.error("检查restl时发现错误 {}", checkInfo.getMessage());
            result.setMessage("检查restl时发现错误");
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

    private ParseRestfulSyntaxTreeUtil.RestfulRootBean parseRestfulRoot(RestfulDslCodeTemplateDto templateDto) {
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
}
