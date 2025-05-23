package org.demo.baoleme.dto.response.salesStats;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SaleTrendData {
    private String dateLabel;  // 对应 SQL 查询中的 date_label
    private BigDecimal value;   // 对应 SQL 查询中的 SUM(total_amount)
}