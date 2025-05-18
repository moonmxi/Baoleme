package org.demo.baoleme.dto.response.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserCreateOrderResponse {
    private Long orderId;
    private String orderNumber;
    private Long storeId;
    private Long addressId;
    private Long couponId;
    private String remark;
    private Double totalPrice;
    private String status;
    private String payUrl;
    private List<OrderItem> items;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class OrderItem {
        private Long productId;
        private String productName;
        private Integer quantity;
        private Double price;
        private String image;
    }

    public Long getStoreId() {
        return this.storeId;
    }

    public List<OrderItem> getItems() {
        return this.items;
    }
}