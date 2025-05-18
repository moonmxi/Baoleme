package org.demo.baoleme.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class UserSearchResponse {
    private List<Product> products;
    private List<Shop> shops;
    private int total;
    private String keyword;
    private int page;
    private int size;

    @Data
    public static class Product {
        private Long productId;
        private String productName;
        private Double price;
        private String shopName;
    }

    @Data
    public static class Shop {
        private Long shopId;
        private String shopName;
        private Double rating;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}