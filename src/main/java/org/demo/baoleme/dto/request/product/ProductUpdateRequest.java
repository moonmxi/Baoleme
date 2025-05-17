package org.demo.baoleme.dto.request.product;

import lombok.Data;
import org.demo.baoleme.pojo.Product;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Integer stock;
    private String image;
    private Product.ProductStatus status;
}
