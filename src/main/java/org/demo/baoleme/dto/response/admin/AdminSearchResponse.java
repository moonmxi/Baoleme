package org.demo.baoleme.dto.response.admin;

import lombok.Data;

import java.util.Map;

@Data
public class AdminSearchResponse {
    private Long storeId;
    private String storeName;
    private Map<String, Long> products;
}