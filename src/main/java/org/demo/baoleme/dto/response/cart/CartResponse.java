package org.demo.baoleme.dto.response.cart;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartResponse {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private String imageUrl;
}
