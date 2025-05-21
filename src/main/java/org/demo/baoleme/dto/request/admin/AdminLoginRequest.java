package org.demo.baoleme.dto.request.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理员登录请求体
 */
@Data
public class AdminLoginRequest {

    @NotNull(message = "管理员ID不能为空")
    private Long adminId;

    @NotNull(message = "密码不能为空")
    private String password;
}