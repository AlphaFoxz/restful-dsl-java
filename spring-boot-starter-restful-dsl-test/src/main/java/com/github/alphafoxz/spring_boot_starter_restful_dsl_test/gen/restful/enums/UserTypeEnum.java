package com.github.alphafoxz.spring_boot_starter_restful_dsl_test.gen.restful.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "用户类型枚举")
@AllArgsConstructor
@Getter
public enum UserTypeEnum {
    /**管理员*/
    ADMIN(0),
    /**普通用户*/
    USER(1);

    private final int value;
}