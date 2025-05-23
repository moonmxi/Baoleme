package org.demo.baoleme.dto.response.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.demo.baoleme.pojo.OrderItem;

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
    private BigDecimal actualPrice;
    private String status;
    private String payUrl;
    private List<OrderItem> items;
    private LocalDateTime createdAt;

    public void setCreateTime(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setActualPrice(BigDecimal actualPrice) {
    }


}