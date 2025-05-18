package org.demo.baoleme.dto.request.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 骑手抢单请求
 */
@Data
public class OrderGrabRequest {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
}