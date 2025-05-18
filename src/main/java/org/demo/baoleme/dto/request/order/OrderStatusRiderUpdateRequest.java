package org.demo.baoleme.dto.request.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 骑手更新订单状态请求
 */
@Data
public class OrderStatusRiderUpdateRequest {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "目标状态不能为空")
    private Integer targetStatus;

}