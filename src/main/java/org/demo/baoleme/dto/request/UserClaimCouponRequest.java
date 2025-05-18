package org.demo.baoleme.dto.request;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Date;

@Data
public class UserClaimCouponRequest {
    private Long couponId;
}
