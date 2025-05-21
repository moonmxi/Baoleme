package org.demo.baoleme.dto.request.cart;

import lombok.Data;


@Data
public class UpdateCartRequest {
    private Long productId;
    private Integer quantity;
}

