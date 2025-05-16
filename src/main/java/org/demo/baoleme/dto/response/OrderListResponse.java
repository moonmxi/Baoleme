package org.demo.baoleme.dto.response;

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
        private Long order_id;
        private String shop_name;
        private String shop_location;
        private String delivery_address;
        private BigDecimal total_amount;
        private Integer estimated_time;
    }
}