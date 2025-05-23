package org.demo.baoleme.dto.response.salesStats;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SaleTrendStatsResponse {
    private List<String> dates;
    private List<Integer> values;
}
