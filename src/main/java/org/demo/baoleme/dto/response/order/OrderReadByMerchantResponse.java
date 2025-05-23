package org.demo.baoleme.dto.response.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderReadByMerchantResponse {
    private Long orderId;
    private Long userName;
    private Integer status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
}
