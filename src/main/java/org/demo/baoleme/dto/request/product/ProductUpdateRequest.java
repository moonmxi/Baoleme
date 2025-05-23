package org.demo.baoleme.dto.request.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.demo.baoleme.pojo.Product;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Integer stock;
    private String image;
    private Integer status; // 仅在 update status 时生效
}
