package org.demo.baoleme.dto.request.user;

import lombok.Data;

/**
 * 骑手订单历史查询请求
 */
@Data
public class UserOrderHistoryRequest {
    private Integer status;
    private String startTime;
    private String endTime;
    private Integer page;
    private Integer pageSize;
}