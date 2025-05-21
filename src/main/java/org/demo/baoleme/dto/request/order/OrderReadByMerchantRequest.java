package org.demo.baoleme.dto.request.order;

import lombok.Data;

@Data
public class OrderReadByMerchantRequest {
    private Long storeId;
    private Integer status;
    private int page = 1;
    private int pageSize = 10;
}
