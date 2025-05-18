package org.demo.baoleme.dto.request.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 取消已接订单请求
 */
@Data
public class OrderCancelRequest {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
}