package org.demo.baoleme.dto.request.user;

import lombok.Data;

@Data
public class UserGetProductRequest {
    private Long shopId;
    private String category;
    private float minPrice;
    private float maxPrice;
    private String sortBy;
}
