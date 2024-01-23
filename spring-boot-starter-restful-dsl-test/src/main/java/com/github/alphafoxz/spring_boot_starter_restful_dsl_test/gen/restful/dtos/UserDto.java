package com.github.alphafoxz.spring_boot_starter_restful_dsl_test.gen.restful.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.List;
import com.github.alphafoxz.spring_boot_starter_restful_dsl_test.gen.restful.enums.UserGenderEnum;
import org.springframework.lang.Nullable;
import com.github.alphafoxz.spring_boot_starter_restful_dsl_test.gen.restful.enums.UserTypeEnum;

@Schema(name = "UserDto", description = "用户dto")
@Accessors(chain = true)
@Getter
@Setter
public class UserDto {
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
    @Schema(name = "email", description = "邮箱")
    @Nullable
    private String email;
    /**
     * @see UserGenderEnum
     */
    @Schema(name = "gender", description = "性别")
    @Nullable
    private Integer gender;
}