package com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

import java.util.List;

@Schema(name = "SdkVersionCheckResponse", description = "版本检查响应")
@Accessors(chain = true)
@Getter
@Setter
public class SdkVersionCheckResponse {
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
    private List<SdkVersionCheckDto> data;
}