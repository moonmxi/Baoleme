package org.demo.baoleme.dto.request.product;

import lombok.Data;
import org.demo.baoleme.pojo.Product;

@Data
public class ProductViewRequest {
    private Long productId;
    private Long storeId;
    private String category;
    private Integer status;
    private int page = 1;   // 默认值
    private int pageSize = 10;
}
