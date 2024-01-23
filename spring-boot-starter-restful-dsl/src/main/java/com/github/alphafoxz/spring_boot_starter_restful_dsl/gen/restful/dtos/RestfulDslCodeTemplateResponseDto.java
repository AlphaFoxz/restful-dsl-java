package com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.dtos.RestfulDslCodeTemplateDto;
import org.springframework.lang.Nullable;

// 响应体的data字段是RestfulDslCodeTemplateDto
@Schema(name = "RestfulDslCodeTemplateResponseDto", description = "代码模板响应实体")
@Accessors(chain = true)
@Getter
@Setter
public class RestfulDslCodeTemplateResponseDto {
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
    private RestfulDslCodeTemplateDto data;
}