package org.demo.baoleme.dto.request.admin;

import lombok.Data;

/**
 * 管理员分页查询用户请求体
 */
@Data
public class AdminUserQueryRequest {
    private String keyword;
    private String gender;
    private Long startId;
    private Long endId;
    private Integer page;
    private Integer pageSize;
}