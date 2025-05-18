package org.demo.baoleme.dto.response.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserGetShopResponse {
    private List<Shop> data;
    private int total;
    private String type;
    private float minRating;
    private float maxRating;
    private int page;
    private int size;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Shop {
        private Long shopId;
        private String shopName;
        private String type;
        private float rating;
        private String deliveryTime;
        private String image;
    }

    public void setData(List<Shop> shops) {
        this.data = shops;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}