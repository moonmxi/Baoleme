package org.demo.baoleme.dto.request.user;

import lombok.Data;
import org.demo.baoleme.pojo.OrderItem;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UserCreateOrderRequest {
    private Long addressId;  // 修正拼写错误：addresId -> addressId
    private Long storeId;
    private Long couponId;
    private String remark;   // 订单备注
    private List<OrderItem> items; // 改为订单项列表

    public String getAddress() {
        return addressId.toString();
    }

    // 内部创建一个简化的DTO用于接收前端数据
    @Data
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
        private BigDecimal price;
        // 注意：这里不需要orderId，因为订单创建时还没有orderId
    }
}