package org.demo.baoleme.dto.response.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderUpdateByMerchantResponse {
    private Long id;
    private Integer oldStatus;
    private Integer newStatus;
    private LocalDateTime updateAt;
    private String cancelReason;
}
