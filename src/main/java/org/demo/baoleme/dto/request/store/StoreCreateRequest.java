package org.demo.baoleme.dto.request.store;

import lombok.Data;
import org.demo.baoleme.pojo.Store;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StoreCreateRequest {
    private Long merchantId;
    private String name;
    private String desc;
    private String location;
    private BigDecimal rating;
    private BigDecimal balance;
    private Store.ShopStatus status;
    private LocalDateTime createdAt;
    private String image;
}
