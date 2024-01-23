package com.github.alphafoxz.restful_dsl.test.gen.restful.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "用户性别枚举")
@AllArgsConstructor
@Getter
public enum UserGenderEnum {
    /**男*/
    MALE(0),
    /**女*/
    FEMALE(1),
    /**未知*/
    UNKNOWN(2);

    private final int value;
}