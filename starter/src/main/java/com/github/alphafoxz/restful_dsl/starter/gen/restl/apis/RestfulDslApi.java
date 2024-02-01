package com.github.alphafoxz.restful_dsl.starter.gen.restl.apis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import com.github.alphafoxz.restful_dsl.starter.standard.HttpController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.alphafoxz.restful_dsl.starter.gen.restl.dtos.RestfulDslCodeTemplateRequestDto;
import com.github.alphafoxz.restful_dsl.starter.gen.restl.dtos.RestfulDslStringResponseDto;
import com.github.alphafoxz.restful_dsl.starter.gen.restl.dtos.RestfulDslMapResponseDto;
import org.springframework.web.bind.annotation.RequestMapping;
import com.github.alphafoxz.restful_dsl.starter.gen.restl.dtos.RestfulDslListResponseDto;
import com.github.alphafoxz.restful_dsl.starter.gen.restl.dtos.RestfulDslVersionCheckResponse;
import com.github.alphafoxz.restful_dsl.starter.gen.restl.dtos.RestfulDslCodeTemplateResponseDto;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.GetMapping;
import com.github.alphafoxz.restful_dsl.starter.gen.restl.dtos.RestfulDslFileTreeResponseDto;

@RequestMapping({"/_restfulDsl"})
@Tag(name = "RestfulDslApi", description = "restful-dsl接口")
public interface RestfulDslApi extends HttpController {
    @PostMapping(value = {"/generateTsClientApi"})
    @Operation(summary = "创建Ts client的Api代码", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public default ResponseEntity<RestfulDslMapResponseDto> generateTsClientApi(
        @RequestBody java.util.Map<String, Object> _requestMap
    ) {
        return generateTsClientApi(U.toBean(_requestMap.get("templateDto"), RestfulDslCodeTemplateRequestDto.class), (String) _requestMap.get("genDir"));
    }

    public ResponseEntity<RestfulDslMapResponseDto> generateTsClientApi(
            RestfulDslCodeTemplateRequestDto templateDto,
            String genDir
    );

    @PostMapping(value = {"/generateRustClientApi"})
    @Operation(summary = "创建rust client的Api代码", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public default ResponseEntity<RestfulDslMapResponseDto> generateRustClientApi(
        @RequestBody java.util.Map<String, Object> _requestMap
    ) {
        return generateRustClientApi(U.toBean(_requestMap.get("templateDto"), RestfulDslCodeTemplateRequestDto.class), (String) _requestMap.get("genDir"));
    }

    public ResponseEntity<RestfulDslMapResponseDto> generateRustClientApi(
            RestfulDslCodeTemplateRequestDto templateDto,
            String genDir
    );

    @PostMapping(value = {"/generateJavaServerApi"})
    @Operation(summary = "创建java server的Api代码", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<RestfulDslListResponseDto> generateJavaServerApi(
            @Parameter(description = "") @RequestBody RestfulDslCodeTemplateRequestDto templateDto
    );

    @PostMapping(value = {"/generateSql"})
    @Operation(summary = "生成sql语句", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<RestfulDslMapResponseDto> generateSql(
            @Parameter(description = "") @RequestBody RestfulDslCodeTemplateRequestDto templateDto
    );

    @PostMapping(value = {"/createOrUpdateFile"})
    @Operation(summary = "创建或更新文件", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public default ResponseEntity<RestfulDslStringResponseDto> createOrUpdateFile(
        @RequestBody java.util.Map<String, Object> _requestMap
    ) {
        return createOrUpdateFile((String) _requestMap.get("filePath"), (String) _requestMap.get("content"));
    }

    public ResponseEntity<RestfulDslStringResponseDto> createOrUpdateFile(
            String filePath,
            String content
    );

    @PostMapping(value = {"/createFolder"})
    @Operation(summary = "创建文件夹", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public default ResponseEntity<RestfulDslStringResponseDto> createFolder(
        @RequestBody java.util.Map<String, Object> _requestMap
    ) {
        return createFolder((String) _requestMap.get("folderPath"));
    }

    public ResponseEntity<RestfulDslStringResponseDto> createFolder(
            String folderPath
    );

    @PostMapping(value = {"/renameFile"})
    @Operation(summary = "重命名文件", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public default ResponseEntity<RestfulDslStringResponseDto> renameFile(
        @RequestBody java.util.Map<String, Object> _requestMap
    ) {
        return renameFile((String) _requestMap.get("filePath"), (String) _requestMap.get("newPath"));
    }

    public ResponseEntity<RestfulDslStringResponseDto> renameFile(
            String filePath,
            String newPath
    );

    @PostMapping(value = {"/deleteFile"})
    @Operation(summary = "删除文件", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public default ResponseEntity<RestfulDslListResponseDto> deleteFile(
        @RequestBody java.util.Map<String, Object> _requestMap
    ) {
        return deleteFile((String) _requestMap.get("filePath"));
    }

    public ResponseEntity<RestfulDslListResponseDto> deleteFile(
            String filePath
    );

    @PostMapping(value = {"/getTemplateContentByPath"})
    @Operation(summary = "根据路径获取内容", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public default ResponseEntity<RestfulDslCodeTemplateResponseDto> getTemplateContentByPath(
        @RequestBody java.util.Map<String, Object> _requestMap
    ) {
        return getTemplateContentByPath((String) _requestMap.get("filePath"));
    }

    public ResponseEntity<RestfulDslCodeTemplateResponseDto> getTemplateContentByPath(
            String filePath
    );

    @GetMapping(value = {"/getRestfulTemplateFileTree"})
    @Operation(summary = "获取restful模板文件树", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<RestfulDslFileTreeResponseDto> getRestfulTemplateFileTree();

    @GetMapping(value = {"/getTemplateContentByImportPath"})
    @Operation(summary = "获取引用文件", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<RestfulDslCodeTemplateResponseDto> getTemplateContentByImportPath(
            @Parameter(description = "") @RequestParam String temp_path,
            @Parameter(description = "") @RequestParam String import_path
    );

    @GetMapping(value = {"/getBasePackage"})
    @Operation(summary = "获取包前缀", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<RestfulDslStringResponseDto> getBasePackage();

    @GetMapping(value = {"/checkRestfulFileVersion"})
    @Operation(summary = "检查restful文件版本与生成情况", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<RestfulDslVersionCheckResponse> checkRestfulFileVersion();

}