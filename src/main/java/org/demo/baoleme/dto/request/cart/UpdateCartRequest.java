package org.demo.baoleme.dto.request.cart;

import lombok.Data;


@Data
public class UpdateCartRequest {
    private Long productId;  // 商品ID
    private int quantity;    // 修改后的数量
}

