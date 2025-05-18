package org.demo.baoleme.dto.response.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员分页查询骑手信息 响应 DTO
 */
@Data
public class AdminRiderQueryResponse {

    private Long id;

    private String username;

    private String phone;

    private Integer order_status;

    private Integer dispatch_mode;

    private Long balance;

    private String avatar;

    private LocalDateTime created_at;
}