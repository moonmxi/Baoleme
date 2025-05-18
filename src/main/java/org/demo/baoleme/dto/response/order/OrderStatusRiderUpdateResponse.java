package org.demo.baoleme.dto.response.order;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 骑手订单状态更新响应
 */
@Data
public class OrderStatusRiderUpdateResponse {
    private Long order_id;
    private Integer status;
    private LocalDateTime updated_at;
}