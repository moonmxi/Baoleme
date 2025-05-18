package org.demo.baoleme.dto.response.order;

import lombok.Data;

/**
 * 取消订单响应
 */
@Data
public class OrderCancelResponse {
    private Long order_id;
    private String status;
}