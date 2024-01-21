package demo.restful.biz_test.gen.restful.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Schema(name = "TestADto", description = "一个测试实体")
@Accessors(chain = true)
@Getter
@Setter
public class TestADto {
    @Schema(name = "id", description = "主键")
    private Long id;
    @Schema(name = "name", description = "名称")
    private String name;
}