package org.demo.baoleme.dto.request.store;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StoreDeleteRequest {
    @NotBlank(message = "店铺id不能为空")
    private Long storeId;
}
