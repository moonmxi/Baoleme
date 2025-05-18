package org.demo.baoleme.dto.response.order;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 骑手订单状态更新响应
 */
@Data
public class OrderStatusRiderUpdateResponse {
    private Long orderId;
    private Integer status;
    private LocalDateTime updatedAt;
}