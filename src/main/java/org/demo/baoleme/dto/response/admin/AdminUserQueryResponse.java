package org.demo.baoleme.dto.response.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员分页查询用户单项响应结构
 */
@Data
public class AdminUserQueryResponse {
    private Long id;
    private String username;
    private String description;
    private String phone;
    private String avatar;
    private LocalDateTime createdAt;
}