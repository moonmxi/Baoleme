package org.demo.baoleme.dto.response.product;

import lombok.Data;

import java.util.List;

@Data
public class ProductPageResponse {
    private List<ProductViewResponse> products;
    private Integer currentPage;
    private Integer totalPages;
    private Integer totalItems;
}
