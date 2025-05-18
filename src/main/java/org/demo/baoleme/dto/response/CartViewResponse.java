package org.demo.baoleme.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartViewResponse {
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;
}