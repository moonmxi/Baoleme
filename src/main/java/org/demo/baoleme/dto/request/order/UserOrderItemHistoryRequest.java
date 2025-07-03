package org.demo.baoleme.dto.request.order;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class UserOrderItemHistoryRequest {
    private Long orderId;
    private Integer page;
    private Integer pageSize;
}
