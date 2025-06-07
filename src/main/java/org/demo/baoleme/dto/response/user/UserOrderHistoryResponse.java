package org.demo.baoleme.dto.response.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手订单历史查询请求
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserOrderHistoryResponse {
    private Long orderId;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;
    private Integer status;
    private String userLocation;
    private String storeLocation;
    private Long storeId;
    private String storeName;
    private String remark;
    private Long riderId;
    private String riderName;
    private String riderPhone;
    private BigDecimal totalPrice;
    private BigDecimal actualPrice;
    private BigDecimal deliveryPrice;
}