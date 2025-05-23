package org.demo.baoleme.dto.request.cart;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Long productId;  // 商品ID
    private int quantity;    // 添加的数量
}

