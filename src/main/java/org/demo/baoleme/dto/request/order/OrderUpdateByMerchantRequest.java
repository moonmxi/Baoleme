package org.demo.baoleme.dto.request.order;

import lombok.Data;

// 或许到时候合并
@Data
public class OrderUpdateByMerchantRequest {
    private Long id;
    private Long storeId;
    private Integer newStatus;
    private String cancelReason;
}
