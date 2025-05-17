package org.demo.baoleme.dto.response.shop;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.demo.baoleme.pojo.Shop;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShopUpdateResponse {
    private Long id;
    private Long merchantId;
    private String name;
    private String type;
    private String location;
    private BigDecimal rating;
    private BigDecimal balance;
    private Shop.ShopStatus status;
    private LocalDateTime createdAt;
}
