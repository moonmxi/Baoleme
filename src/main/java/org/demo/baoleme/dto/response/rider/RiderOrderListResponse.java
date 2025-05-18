package org.demo.baoleme.dto.response.rider;

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
        private Long orderId;
        private String status;
        private BigDecimal totalAmount;
        private BigDecimal deliveryFee;
        private LocalDateTime completedAt;
    }
}