package org.demo.baoleme.dto.response.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.demo.baoleme.pojo.Product;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductUpdateResponse {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Integer stock;
    private String image;
    private Integer status;
}
