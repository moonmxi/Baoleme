package org.demo.baoleme.dto.request.user;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserGetShopRequest {
    private String type;
    private BigDecimal distance;
    private BigDecimal startRating;
    private BigDecimal  endRating;
    private int page;
    private int size;
}
