package org.demo.baoleme.dto.request.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// 或许到时候合并
@Data
public class OrderUpdateByMerchantRequest {
    @NotNull(message = "订单ID不能为空")
    private Long id;

    @NotNull(message = "需设置新的状态")
    private Integer newStatus;

    private String cancelReason;
}
