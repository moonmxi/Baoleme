package org.demo.baoleme.dto.request.admin;

import lombok.Data;

/**
 * 管理员分页查询用户请求体
 */
@Data
public class AdminUserQueryRequest {
    private Integer page;
    private Integer pageSize;
}