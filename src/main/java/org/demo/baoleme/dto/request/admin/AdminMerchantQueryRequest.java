package org.demo.baoleme.dto.request.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminMerchantQueryRequest {

    @NotNull
    @Min(1)
    private Integer page;

    @NotNull
    @Min(1)
    private Integer pageSize;
}