package demo.restful.biz_test.gen.restful.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import demo.restful.biz_test.gen.restful.enums.TestEnum;

@Schema(name = "TestADto", description = "一个测试实体")
@Accessors(chain = true)
@Getter
@Setter
public class TestADto {
    @Schema(name = "id", description = "主键")
    private Long id;
    @Schema(name = "name", description = "名称")
    private String name;
    /**
     * @see TestEnum
     */
    @Schema(name = "type", description = "类型")
    private int type;
}