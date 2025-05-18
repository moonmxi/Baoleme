package org.demo.baoleme.dto.response.admin;

import lombok.Data;

/**
 * 管理员登录响应体
 */
@Data
public class AdminLoginResponse {

    private Long id;
    private String token;
}