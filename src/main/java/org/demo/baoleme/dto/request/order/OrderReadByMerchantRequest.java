package org.demo.baoleme.dto.request.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderReadByMerchantRequest {
    @NotNull(message = "店铺ID不能为空")
    private Long storeId;

    private Integer status;

    private int page = 1;
    private int pageSize = 10;
}
