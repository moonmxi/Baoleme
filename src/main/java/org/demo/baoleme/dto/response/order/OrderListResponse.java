package org.demo.baoleme.dto.response.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 可抢订单列表响应
 */
@Data
public class OrderListResponse {
    private List<OrderBrief> orders;

    @Data
    public static class OrderBrief {
        private Long orderId;
        private String shopName;
        private String shopLocation;
        private String deliveryAddress;
        private BigDecimal totalAmount;
        private Integer estimatedTime;
    }
}