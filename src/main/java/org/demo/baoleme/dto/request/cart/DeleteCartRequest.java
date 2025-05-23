package org.demo.baoleme.dto.request.cart;

import lombok.Data;


@Data
public class DeleteCartRequest {
    private Long productId;  // 要删除的商品ID
}