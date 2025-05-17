package org.demo.baoleme.dto.response.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductViewResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer monthSales;
    private BigDecimal rating;
    private String image;
}
