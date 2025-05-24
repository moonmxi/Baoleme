package org.demo.baoleme.dto.response.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserCurrentOrderResponse {
    private Long orderId;
    private LocalDateTime createdAt;
    private Integer status;
    private String remark;
    private String storeName;
    private String riderName;
    private String riderPhone;
}