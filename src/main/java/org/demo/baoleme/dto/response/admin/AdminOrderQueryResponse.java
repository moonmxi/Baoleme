package org.demo.baoleme.dto.response.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员订单分页查询响应
 */
@Data
public class AdminOrderQueryResponse {

    private Long orderId;
    private Long userId;
    private Long storeId;
    private Long riderId;
    private Integer status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private LocalDateTime endedAt;
}