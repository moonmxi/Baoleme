package org.demo.baoleme.dto.request.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductViewRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "店铺ID不能为空")
    private Long storeId;

    private String category;
    private Integer status;

    private int page = 1;   // 默认值
    private int pageSize = 10;
}
