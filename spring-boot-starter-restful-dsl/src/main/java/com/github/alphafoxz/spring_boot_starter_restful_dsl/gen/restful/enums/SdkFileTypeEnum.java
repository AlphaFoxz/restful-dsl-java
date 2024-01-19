package com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.enums;

import com.github.alphafoxz.spring_boot_starter_restful_dsl.stardard.RestfulEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "文件类型")
@AllArgsConstructor
@Getter
public enum SdkFileTypeEnum implements RestfulEnum {
    /**
     * 本地文件
     */
    LOCAL_FILE(0),
    /**
     * 本地文件夹
     */
    LOCAL_DIR(1);

    private final int value;
}