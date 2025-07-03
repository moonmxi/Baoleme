package org.demo.baoleme.dto.response.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.demo.baoleme.pojo.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserCreateOrderResponse {
    private Long orderId;
    private Long storeId;
    private String storeName;
    private String remark;
    private BigDecimal totalPrice;
    private BigDecimal actualPrice;
    private Integer status;
    private List<Map<String, Object>> items;
    private LocalDateTime createdAt;
}