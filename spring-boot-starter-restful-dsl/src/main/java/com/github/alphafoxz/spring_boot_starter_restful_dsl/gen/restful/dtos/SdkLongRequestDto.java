package com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Schema(name = "SdkLongRequestDto", description = "")
@Accessors(chain = true)
@Getter
@Setter
public class SdkLongRequestDto {
    @Schema(name = "id", description = "")
    private Long id;
    @Schema(name = "taskId", description = "")
    private Long taskId;
    @Schema(name = "data", description = "")
    private Long data;
}