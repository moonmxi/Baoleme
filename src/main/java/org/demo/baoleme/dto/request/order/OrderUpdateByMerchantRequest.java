package org.demo.baoleme.dto.request.order;

import lombok.Data;
import org.demo.baoleme.pojo.Order;

// 或许到时候合并
@Data
public class OrderUpdateByMerchantRequest {
    private Long id;
    private Order.OrderStatus newStatus;
    private String cancelReason;
}
