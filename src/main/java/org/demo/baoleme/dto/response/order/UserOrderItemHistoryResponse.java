package org.demo.baoleme.dto.response.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Data
public class UserOrderItemHistoryResponse {
    private List<Map<String, Object>> orderItemList;
    private Map<String, BigDecimal> priceInfo;
}
