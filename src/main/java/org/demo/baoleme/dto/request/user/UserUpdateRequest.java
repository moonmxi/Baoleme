package org.demo.baoleme.dto.request.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
    @Size(min = 2, max = 20, message = "用户名长度应为2~20个字符")
    private String username;

    @Size(min = 6, max = 20, message = "密码长度应为6~20个字符")
    private String password;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不合法")
    private String phone;

}
