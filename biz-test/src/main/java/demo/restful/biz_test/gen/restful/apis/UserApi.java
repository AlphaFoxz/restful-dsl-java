package demo.restful.biz_test.gen.restful.apis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import demo.restful.biz_test.standard.HttpController;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import demo.restful.biz_test.standard.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import demo.restful.biz_test.gen.restful.dtos.UserDto;
import demo.restful.biz_test.gen.restful.dtos.UserSimpleDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.lang.Nullable;

@RequestMapping({"/user"})
@Tag(name = "UserApi", description = "用户接口")
public interface UserApi extends HttpController {
    @GetMapping(value = {"/query/{id}"})
    @Operation(summary = "根据id查询用户", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<UserDto> query(
            @Parameter(description = "主键") @PathVariable("id") Long id
    );

    // 无需增加实体，就像调用方法一样去设计这个接口
    @GetMapping(value = {"/queryPage/{pageNum}/{pageSize}"})
    @Operation(summary = "查询分页", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<Page<UserDto>> queryPage(
            @Parameter(description = "页码") @PathVariable("pageNum") Integer pageNum,
            @Parameter(description = "每页条数") @PathVariable("pageSize") Integer pageSize,
            @Parameter(description = "可选 筛选用户名") @Nullable @RequestParam String username
    );

    @PostMapping(value = {"/register"})
    @Operation(summary = "注册用户", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<UserDto> register();

    @PostMapping(value = {"/update"})
    @Operation(summary = "修改用户信息", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<UserSimpleDto> update(
            @Parameter(description = "用户信息") @RequestBody UserSimpleDto user
    );

    @GetMapping(value = {"/delete/{id}"})
    @Operation(summary = "删除用户", responses = {
            @ApiResponse(description = "请求成功", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "无权限", responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(description = "参数无效", responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<Boolean> delete(
            @Parameter(description = "主键") @PathVariable("id") Long id
    );

}