package org.demo.baoleme.dto.request.order;

import lombok.Data;
import org.demo.baoleme.pojo.Order;

@Data
public class OrderViewRequest {
    private Long storeId;
    private Order.OrderStatus status;
}
