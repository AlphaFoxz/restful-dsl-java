package com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Schema(name = "RestfulDslVersionCheckDto", description = "版本检查结果")
@Accessors(chain = true)
@Getter
@Setter
public class RestfulDslVersionCheckDto {
    @Schema(name = "filePath", description = "")
    private String filePath;
    @Schema(name = "sha256", description = "")
    private String sha256;
    @Schema(name = "same", description = "")
    private Boolean same;
    @Schema(name = "message", description = "")
    private String message;
}