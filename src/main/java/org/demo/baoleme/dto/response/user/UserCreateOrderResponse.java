package org.demo.baoleme.dto.response.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private BigDecimal totalPrice;
    private String status;
    private String payUrl;
    private List<OrderItem> items;
    private LocalDateTime createdAt;

    public void setCreateTime(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

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