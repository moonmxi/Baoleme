package org.demo.baoleme.dto.request.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDeleteRequest {
    @NotNull(message = "商品ID不能为空")
    private Long id;
}
