package org.demo.baoleme.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 骑手订单历史查询请求
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserOrderHistoryResponse {
    private Integer status;
    private String start_time;
    private String end_time;
    private Integer page;
    private Integer page_size;
}