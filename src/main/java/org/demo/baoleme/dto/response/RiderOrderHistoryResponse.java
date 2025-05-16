package org.demo.baoleme.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手历史订单响应项
 */
@Data
public class RiderOrderHistoryResponse {
    private Long order_id;
    private Integer status;
    private BigDecimal total_amount;
    private LocalDateTime completed_at;
}