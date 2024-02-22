package com.github.alphafoxz.restful_dsl.starter.gen.restl.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "RestfulDsl服务端语言类型")
@AllArgsConstructor
@Getter
public enum RestfulDslServerLanguageTypeEnum {
    /**JAVA*/
    JAVA(0);

    private final int value;
}