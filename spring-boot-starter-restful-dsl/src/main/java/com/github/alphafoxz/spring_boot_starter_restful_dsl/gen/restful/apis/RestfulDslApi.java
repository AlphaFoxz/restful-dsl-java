package com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.apis;

import com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.dtos.*;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.stardard.HttpController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping({"/_restfulDsl"})
@Tag(name = "RestfulDslApi", description = "restful-dsl接口")
public interface RestfulDslApi extends HttpController {
    @PostMapping(value = {"/generateTsClientApi"})
    @Operation(summary = "创建Ts client的Api代码", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public default ResponseEntity<SdkMapResponseDto> generateTsClientApi(
            @RequestBody java.util.Map<String, Object> _requestMap
    ) {
        return generateTsClientApi(U.toBean(_requestMap.get("templateDto"), SdkCodeTemplateRequestDto.class), (String) _requestMap.get("genDir"));
    }

    public ResponseEntity<SdkMapResponseDto> generateTsClientApi(
            SdkCodeTemplateRequestDto templateDto,
            String genDir
    );

    @PostMapping(value = {"/generateRustClientApi"})
    @Operation(summary = "创建rust client的Api代码", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public default ResponseEntity<SdkMapResponseDto> generateRustClientApi(
            @RequestBody java.util.Map<String, Object> _requestMap
    ) {
        return generateRustClientApi(U.toBean(_requestMap.get("templateDto"), SdkCodeTemplateRequestDto.class), (String) _requestMap.get("genDir"));
    }

    public ResponseEntity<SdkMapResponseDto> generateRustClientApi(
            SdkCodeTemplateRequestDto templateDto,
            String genDir
    );

    @PostMapping(value = {"/generateJavaServerApi"})
    @Operation(summary = "创建java server的Api代码", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<SdkListResponseDto> generateJavaServerApi(
            @Parameter(description = "") @RequestBody SdkCodeTemplateRequestDto templateDto
    );

    @PostMapping(value = {"/generateSql"})
    @Operation(summary = "生成sql语句", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<SdkMapResponseDto> generateSql(
            @Parameter(description = "") @RequestBody SdkCodeTemplateRequestDto templateDto
    );

    @PostMapping(value = {"/createOrUpdateFile"})
    @Operation(summary = "创建或更新文件", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public default ResponseEntity<SdkStringResponseDto> createOrUpdateFile(
            @RequestBody java.util.Map<String, Object> _requestMap
    ) {
        return createOrUpdateFile((String) _requestMap.get("filePath"), (String) _requestMap.get("content"));
    }

    public ResponseEntity<SdkStringResponseDto> createOrUpdateFile(
            String filePath,
            String content
    );

    @PostMapping(value = {"/createFolder"})
    @Operation(summary = "创建文件夹", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public default ResponseEntity<SdkStringResponseDto> createFolder(
            @RequestBody java.util.Map<String, Object> _requestMap
    ) {
        return createFolder((String) _requestMap.get("folderPath"));
    }

    public ResponseEntity<SdkStringResponseDto> createFolder(
            String folderPath
    );

    @PostMapping(value = {"/renameFile"})
    @Operation(summary = "重命名文件", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public default ResponseEntity<SdkStringResponseDto> renameFile(
            @RequestBody java.util.Map<String, Object> _requestMap
    ) {
        return renameFile((String) _requestMap.get("filePath"), (String) _requestMap.get("newPath"));
    }

    public ResponseEntity<SdkStringResponseDto> renameFile(
            String filePath,
            String newPath
    );

    @PostMapping(value = {"/deleteFile"})
    @Operation(summary = "删除文件", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public default ResponseEntity<SdkListResponseDto> deleteFile(
            @RequestBody java.util.Map<String, Object> _requestMap
    ) {
        return deleteFile((String) _requestMap.get("filePath"));
    }

    public ResponseEntity<SdkListResponseDto> deleteFile(
            String filePath
    );

    @PostMapping(value = {"/getTemplateContentByPath"})
    @Operation(summary = "根据路径获取内容", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public default ResponseEntity<SdkCodeTemplateResponseDto> getTemplateContentByPath(
            @RequestBody java.util.Map<String, Object> _requestMap
    ) {
        return getTemplateContentByPath((String) _requestMap.get("filePath"));
    }

    public ResponseEntity<SdkCodeTemplateResponseDto> getTemplateContentByPath(
            String filePath
    );

    @GetMapping(value = {"/getRestfulTemplateFileTree"})
    @Operation(summary = "获取restful模板文件树", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<SdkFileTreeResponseDto> getRestfulTemplateFileTree();

    @GetMapping(value = {"/getTemplateContentByImportPath"})
    @Operation(summary = "", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<SdkCodeTemplateResponseDto> getTemplateContentByImportPath(
            @Parameter(description = "") @RequestParam String temp_path,
            @Parameter(description = "") @RequestParam String import_path
    );

    @GetMapping(value = {"/getBasePackage"})
    @Operation(summary = "获取文件包前缀", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<SdkStringResponseDto> getBasePackage();

}