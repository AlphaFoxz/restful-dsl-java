package com.github.alphafoxz.restful_dsl.test.gen.restl.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.List;
import com.github.alphafoxz.restful_dsl.test.gen.restl.enums.UserTypeEnum;

@Schema(name = "UserSimpleDto", description = "用户简易dto")
@Accessors(chain = true)
@Getter
@Setter
public class UserSimpleDto {
    @Schema(name = "id", description = "主键")
    private Long id;
    @Schema(name = "username", description = "姓名")
    private String username;
    /**
     * @see UserTypeEnum
     */
    @Schema(name = "userType", description = "用户类型")
    private Integer userType;
    @Schema(name = "roles", description = "权限")
    private List<String> roles;
}