package org.demo.baoleme.dto.request.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 骑手订单历史查询请求
 */
@Data
public class UserOrderHistoryRequest {
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer page;
    private Integer pageSize;
}