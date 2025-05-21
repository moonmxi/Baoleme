package org.demo.baoleme.dto.response.user;

import lombok.Data;

import java.util.Map;

@Data
public class UserSearchResponse {
    private Long storeId;
    private String storeName;
    private Map<String, Long> products;
}


