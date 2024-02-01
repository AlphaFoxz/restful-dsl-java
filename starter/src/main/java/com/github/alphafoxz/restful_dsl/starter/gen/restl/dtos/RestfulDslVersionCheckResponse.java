package com.github.alphafoxz.restful_dsl.starter.gen.restl.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.List;
import org.springframework.lang.Nullable;

@Schema(name = "RestfulDslVersionCheckResponse", description = "版本检查响应")
@Accessors(chain = true)
@Getter
@Setter
public class RestfulDslVersionCheckResponse {
    @Schema(name = "id", description = "")
    @Nullable
    private Long id;
    @Schema(name = "taskId", description = "")
    private Long taskId;
    @Schema(name = "success", description = "")
    private Boolean success;
    @Schema(name = "message", description = "")
    @Nullable
    private String message;
    @Schema(name = "data", description = "")
    private List<RestfulDslVersionCheckDto> data;
}