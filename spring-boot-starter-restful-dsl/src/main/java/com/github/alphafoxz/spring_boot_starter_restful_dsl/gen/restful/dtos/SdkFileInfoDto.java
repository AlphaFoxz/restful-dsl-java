package com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

import java.util.List;

@Schema(name = "SdkFileInfoDto", description = "")
@Accessors(chain = true)
@Getter
@Setter
public class SdkFileInfoDto {
    @Schema(name = "filePath", description = "")
    private String filePath;
    @Schema(name = "parentDir", description = "")
    private String parentDir;
    @Schema(name = "fileName", description = "")
    private String fileName;
    @Schema(name = "separator", description = "")
    private String separator;
    @Schema(name = "content", description = "")
    @Nullable
    private String content;
    @Schema(name = "ext", description = "")
    @Nullable
    private String ext;
    @Schema(name = "fileType", description = "")
    private int fileType;
    @Schema(name = "isReadOnly", description = "")
    private Boolean isReadOnly;
    @Schema(name = "isEmpty", description = "")
    private Boolean isEmpty;
    @Schema(name = "children", description = "")
    @Nullable
    private List<SdkFileInfoDto> children;
}