package com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Schema(name = "SdkMapRequestDto", description = "")
@Accessors(chain = true)
@Getter
@Setter
public class SdkMapRequestDto {
    @Schema(name = "id", description = "")
    private Long id;
    @Schema(name = "taskId", description = "")
    private Long taskId;
    @Schema(name = "data", description = "")
    private Map<String, String> data;
}