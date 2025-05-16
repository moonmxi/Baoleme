package org.demo.baoleme.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 骑手更新订单状态请求
 */
@Data
public class OrderStatusRiderUpdateRequest {
    @NotNull(message = "订单ID不能为空")
    private Long order_id;

    @NotNull(message = "目标状态不能为空")
    private Integer target_status;

    @NotNull(message = "操作时间不能为空")
    private LocalDateTime timestamp;
}