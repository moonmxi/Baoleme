package org.demo.baoleme.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private String imageUrl;
}
