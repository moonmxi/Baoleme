package org.demo.baoleme.dto.request.store;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.demo.baoleme.pojo.Store;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StoreViewInfoRequest {
    @NotBlank(message = "店铺id不能为空")
    private Long storeId;
}
