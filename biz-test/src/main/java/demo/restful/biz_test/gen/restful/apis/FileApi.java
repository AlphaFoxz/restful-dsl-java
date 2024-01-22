package demo.restful.biz_test.gen.restful.apis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import demo.restful.biz_test.standard.HttpController;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.lang.Nullable;

@RequestMapping({"/file"})
@Tag(name = "FileApi", description = "文件接口")
public interface FileApi extends HttpController {
    // 客户端对应类似这样一个表单：
    // <form>
    // <input name="file" title="*请上传文件" type="file" />
    // <input name="desc" title="[可选]输入一段描述" />
    // </from>
    @PostMapping(value = {"/upload"}, consumes = "multipart/form-data")
    @Operation(summary = "上传文件", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<Boolean> upload(
            @Parameter(description = "二进制文件") MultipartFile file,
            @Parameter(description = "可选说明") @Nullable String desc
    );

    // 当返回值为list<byte>时，实际上生成的代码会自动优化为byte数组，而不是反直觉的list容器
    @GetMapping(value = {"/download"})
    @Operation(summary = "下载文件", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<byte[]> download(
            @Parameter(description = "主键") @RequestParam Long id
    );

}