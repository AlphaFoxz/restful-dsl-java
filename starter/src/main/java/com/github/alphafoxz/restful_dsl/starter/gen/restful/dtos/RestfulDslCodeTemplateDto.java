package com.github.alphafoxz.restful_dsl.starter.gen.restful.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.Map;
import org.springframework.lang.Nullable;

@Schema(name = "RestfulDslCodeTemplateDto", description = "代码模板实体")
@Accessors(chain = true)
@Getter
@Setter
public class RestfulDslCodeTemplateDto {
    @Schema(name = "filePath", description = "文件路径")
    private String filePath;
    @Schema(name = "fileSeparator", description = "系统分隔符")
    private String fileSeparator;
    @Schema(name = "namespace", description = "命名空间")
    private Map<String, String> namespace;
    @Schema(name = "ast", description = "抽象语法树")
    @Nullable
    private String ast;
    @Schema(name = "content", description = "文件内容")
    private String content;
    @Schema(name = "imports", description = "包含其他模板")
    private Map<String, RestfulDslCodeTemplateDto> imports;
}