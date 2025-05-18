package org.demo.baoleme.dto.response.order;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 骑手抢单响应
 */
@Data
public class OrderGrabResponse {
    private Long order_id;
    private String status = "ACCEPTED";
    private LocalDateTime pickup_deadline;
}