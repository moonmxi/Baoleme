package org.demo.baoleme.dto.response.rider;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 骑手收入统计
 */
@Data
public class RiderEarningsResponse {
    private BigDecimal totalEarnings;
    private BigDecimal currentMonth;
    private Integer completedOrders;
}