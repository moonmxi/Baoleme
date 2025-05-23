package org.demo.baoleme.dto.response.rider;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手历史订单响应项
 */
@Data
public class RiderOrderHistoryResponse {
    private Long orderId;
    private Integer status;
    private BigDecimal totalAmount;
    private LocalDateTime completedAt;
}