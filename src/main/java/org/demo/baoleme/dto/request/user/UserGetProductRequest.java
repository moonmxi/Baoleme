package org.demo.baoleme.dto.request.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserGetProductRequest {
    private Long shopId;
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer page;
    private Integer pageSize;
}
