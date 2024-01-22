package demo.restful.biz_test.gen.restful.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.List;
import org.springframework.lang.Nullable;
import demo.restful.biz_test.gen.restful.enums.UserGenderEnum;
import demo.restful.biz_test.gen.restful.enums.UserTypeEnum;

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
    @Schema(name = "gender", description = "性别")
    @Nullable
    private UserGenderEnum gender;
}