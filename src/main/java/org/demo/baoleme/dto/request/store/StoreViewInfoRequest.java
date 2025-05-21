package org.demo.baoleme.dto.request.store;

import lombok.Data;
import org.demo.baoleme.pojo.Store;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StoreViewInfoRequest {
    private Long storeId;
}
