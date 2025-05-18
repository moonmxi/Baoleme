package org.demo.baoleme.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserGetProductResponse {
    private List<Product> data;
    private Long shopId;
    private String category;
    private float minPrice;
    private float maxPrice;
    private String sortBy;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Product {
        private Long productId;
        private String productName;
        private float price;
        private Integer sales;
        private String image;
        private String description;
    }

    public void setData(List<Product> products) {
        this.data = products;
    }
}