package org.demo.baoleme.dto.request.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminStoreQueryRequest {
    private String keyword;
    private String type;
    private BigDecimal  startRating;
    private BigDecimal  endRating;
    private Integer status;
    private Integer page;
    private Integer pageSize;
}