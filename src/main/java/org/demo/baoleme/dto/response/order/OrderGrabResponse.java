package org.demo.baoleme.dto.response.order;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 骑手抢单响应
 */
@Data
public class OrderGrabResponse {
    private Long orderId;
    private String status = "ACCEPTED";
    private LocalDateTime pickupDeadline;
}