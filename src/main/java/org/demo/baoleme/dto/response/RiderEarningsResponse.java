package org.demo.baoleme.dto.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 骑手收入统计
 */
@Data
public class RiderEarningsResponse {
    private BigDecimal total_earnings;
    private BigDecimal current_month;
    private Integer completed_orders;
}