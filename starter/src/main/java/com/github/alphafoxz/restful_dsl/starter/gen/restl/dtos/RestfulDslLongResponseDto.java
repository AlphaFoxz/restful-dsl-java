package com.github.alphafoxz.restful_dsl.starter.gen.restl.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

// 响应体的data字段是long
@Schema(name = "RestfulDslLongResponseDto", description = "长整型响应实体")
@Accessors(chain = true)
@Getter
@Setter
public class RestfulDslLongResponseDto {
    @Schema(name = "id", description = "主键")
    private Long id;
    @Schema(name = "taskId", description = "任务id")
    private Long taskId;
    @Schema(name = "success", description = "是否成功")
    private Boolean success;
    @Schema(name = "message", description = "消息")
    @Nullable
    private String message;
    @Schema(name = "data", description = "数据内容")
    @Nullable
    private Long data;
}