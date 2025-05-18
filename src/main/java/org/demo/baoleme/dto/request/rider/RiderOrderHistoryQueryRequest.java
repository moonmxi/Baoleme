package org.demo.baoleme.dto.request.rider;

import lombok.Data;

/**
 * 骑手订单历史查询请求
 */
@Data
public class RiderOrderHistoryQueryRequest {
    private Integer status;
    private String start_time;
    private String end_time;
    private Integer page;
    private Integer page_size;
}