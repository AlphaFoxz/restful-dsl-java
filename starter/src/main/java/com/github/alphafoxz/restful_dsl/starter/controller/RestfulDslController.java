package com.github.alphafoxz.restful_dsl.starter.controller;

import com.github.alphafoxz.restful_dsl.starter.gen.restful.apis.RestfulDslApi;
import com.github.alphafoxz.restful_dsl.starter.gen.restful.dtos.*;
import com.github.alphafoxz.restful_dsl.starter.service.RestfulDslInfoService;
import com.github.alphafoxz.restful_dsl.starter.service.gen.RestfulDslGenCodeService;
import com.github.alphafoxz.restful_dsl.starter.service.version.RestfulDslVersionCheckService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestfulDslController implements RestfulDslApi {
    @Resource
    private RestfulDslGenCodeService restfulDslGenCodeService;
    @Resource
    private RestfulDslInfoService restfulDslInfoService;
    @Resource
    private RestfulDslVersionCheckService restfulDslVersionCheckService;

    @Override
    public ResponseEntity<RestfulDslMapResponseDto> generateTsClientApi(RestfulDslCodeTemplateRequestDto templateDto, String genDir) {
        return ResponseEntity.ok(restfulDslGenCodeService.previewGenerateTsApi(templateDto, genDir));
    }

    @Override
    public ResponseEntity<RestfulDslMapResponseDto> generateRustClientApi(RestfulDslCodeTemplateRequestDto templateDto, String genDir) {
        return ResponseEntity.ok(restfulDslGenCodeService.previewGenerateRustApi(templateDto, genDir));
    }

    @Override
    public ResponseEntity<RestfulDslListResponseDto> generateJavaServerApi(RestfulDslCodeTemplateRequestDto templateDto) {
        return ResponseEntity.ok(restfulDslGenCodeService.generateJavaApi(templateDto));
    }

    @Override
    public ResponseEntity<RestfulDslMapResponseDto> generateSql(RestfulDslCodeTemplateRequestDto templateDto) {
        return ResponseEntity.ok(restfulDslGenCodeService.previewGenerateSql(templateDto));
    }

    @Override
    public ResponseEntity<RestfulDslStringResponseDto> createOrUpdateFile(String filePath, String content) {
        return ResponseEntity.ok(restfulDslInfoService.createOrUpdateFile(filePath, content));
    }

    @Override
    public ResponseEntity<RestfulDslStringResponseDto> createFolder(String folderPath) {
        return ResponseEntity.ok(restfulDslInfoService.createFolder(folderPath));
    }

    @Override
    public ResponseEntity<RestfulDslStringResponseDto> renameFile(String filePath, String newPath) {
        return ResponseEntity.ok(restfulDslInfoService.renameFile(filePath, newPath));
    }

    @Override
    public ResponseEntity<RestfulDslListResponseDto> deleteFile(String filePath) {
        return ResponseEntity.ok(restfulDslInfoService.deleteFile(filePath));
    }

    @Override
    public ResponseEntity<RestfulDslCodeTemplateResponseDto> getTemplateContentByPath(String filePath) {
        return ResponseEntity.ok(restfulDslInfoService.getTemplateContentByPath(filePath));
    }

    @Override
    public ResponseEntity<RestfulDslFileTreeResponseDto> getRestfulTemplateFileTree() {
        return ResponseEntity.ok(restfulDslInfoService.getRestfulTemplateFileTree());
    }

    @Override
    public ResponseEntity<RestfulDslCodeTemplateResponseDto> getTemplateContentByImportPath(String temp_path, String import_path) {
        return ResponseEntity.ok(restfulDslInfoService.getTemplateContentByImportPath(temp_path, import_path));
    }

    @Override
    public ResponseEntity<RestfulDslStringResponseDto> getBasePackage() {
        return ResponseEntity.ok(restfulDslInfoService.getBasePackage());
    }

    @Override
    public ResponseEntity<RestfulDslVersionCheckResponse> checkRestfulFileVersion() {
        return ResponseEntity.ok(restfulDslVersionCheckService.checkRestfulJava(null));
    }
}
