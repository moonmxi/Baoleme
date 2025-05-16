package org.demo.baoleme.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 骑手订单历史列表响应
 */
@Data
public class RiderOrderListResponse {
    private List<OrderRecord> orders;

    @Data
    public static class OrderRecord {
        private Long order_id;
        private String status;
        private BigDecimal total_amount;
        private BigDecimal delivery_fee;
        private LocalDateTime completed_at;
    }
}