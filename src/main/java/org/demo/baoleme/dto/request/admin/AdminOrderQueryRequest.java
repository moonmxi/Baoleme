package org.demo.baoleme.dto.request.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员订单分页查询请求
 */
@Data
public class AdminOrderQueryRequest {

    @NotNull
    @Min(1)
    private Integer page;

    @NotNull
    @Min(1)
    private Integer pageSize;

    private Long userId;
    private Long storeId;
    private Long riderId;
    private Integer status;

    private LocalDateTime createdAt; // 示例格式：2025-05-18
    private LocalDateTime endedAt;   // 示例格式：2025-05-20
}