package org.demo.baoleme.dto.response.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserCouponResponse {
    private Long couponId;
    private String type;
    private BigDecimal discount;
    private Date expirationDate;
    private Integer fullAmount;
    private Integer reduceAmount;
}
