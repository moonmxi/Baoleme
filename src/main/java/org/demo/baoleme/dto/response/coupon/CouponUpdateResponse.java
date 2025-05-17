package org.demo.baoleme.dto.response.coupon;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.demo.baoleme.pojo.Coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CouponUpdateResponse {
    private String desc;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Coupon.CouponType type;
    private BigDecimal discount;
    private int fullAmount;
    private int reduceAmount;
}
