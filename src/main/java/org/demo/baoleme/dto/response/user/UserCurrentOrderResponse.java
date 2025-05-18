package org.demo.baoleme.dto.response.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserCurrentOrderResponse {
    private List<OrderItem> data;
    private String predictTime;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class OrderItem {
        private Long productId;
        private String productName;
        private Date createTime;
        private Long storeId;
        private String storeName;
    }

    public void setData(List<OrderItem> orderItems) {
        this.data = orderItems;
    }

    public void setPredictTime(String predictTime) {
        this.predictTime = predictTime;
    }
}