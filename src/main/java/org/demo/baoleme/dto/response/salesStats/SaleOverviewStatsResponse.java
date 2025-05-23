package org.demo.baoleme.dto.response.salesStats;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.demo.baoleme.pojo.Product;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SaleOverviewStatsResponse {
    private BigDecimal totalSales;
    private int orderCount;
    private List<Product> popularProducts;
}
