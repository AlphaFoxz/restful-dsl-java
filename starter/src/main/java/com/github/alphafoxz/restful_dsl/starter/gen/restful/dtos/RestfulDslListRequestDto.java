package com.github.alphafoxz.restful_dsl.starter.gen.restful.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.List;

@Schema(name = "RestfulDslListRequestDto", description = "")
@Accessors(chain = true)
@Getter
@Setter
public class RestfulDslListRequestDto {
    @Schema(name = "id", description = "")
    private Long id;
    @Schema(name = "taskId", description = "")
    private Long taskId;
    @Schema(name = "data", description = "")
    private List<String> data;
}