package org.demo.baoleme.dto.response.cart;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartViewResponse {
    private List<CartResponse> items;
    private BigDecimal totalPrice;
}