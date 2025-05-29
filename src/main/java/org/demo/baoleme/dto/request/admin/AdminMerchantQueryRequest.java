package org.demo.baoleme.dto.request.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminMerchantQueryRequest {
    private String keyword;
    private Long  startId;
    private Long endId;
    private Integer page;
    private Integer pageSize;
}