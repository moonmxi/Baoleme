package org.demo.baoleme.dto.request.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Integer stock;
    private String image;
}
