package org.demo.baoleme.dto.response.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.demo.baoleme.pojo.Order;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderUpdateByMerchantResponse {
    private Long id;
    private Order.OrderStatus oldStatus;
    private Order.OrderStatus newStatus;
    private LocalDateTime updateAt;
}
