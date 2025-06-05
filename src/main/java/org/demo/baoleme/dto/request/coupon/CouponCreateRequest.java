package org.demo.baoleme.dto.request.coupon;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CouponCreateRequest {
    @NotNull
    private Long storeId;

    /**
     * 优惠券类型
     * 1-折扣券 2-满减券
     */
    @NotNull
    private Integer type;
    /**折扣率（type=1时有效）*/
    private BigDecimal discount;
    /**满减条件金额（type=2时有效）*/
    private BigDecimal fullAmount;
    /**满减优惠金额（type=2时有效）*/
    private BigDecimal reduceAmount;
}
