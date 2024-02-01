package com.github.alphafoxz.restful_dsl.starter.gen.restl.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Schema(name = "RestfulDslLongRequestDto", description = "")
@Accessors(chain = true)
@Getter
@Setter
public class RestfulDslLongRequestDto {
    @Schema(name = "id", description = "")
    private Long id;
    @Schema(name = "taskId", description = "")
    private Long taskId;
    @Schema(name = "data", description = "")
    private Long data;
}