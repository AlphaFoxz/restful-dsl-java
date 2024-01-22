package demo.restful.biz_test.gen.restful.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "测试枚举类型")
@AllArgsConstructor
@Getter
public enum TestEnum {
    /**具体业务1*/
    TYPE1(0),
    /**具体业务2*/
    TYPE2(1);

    private final int value;
}