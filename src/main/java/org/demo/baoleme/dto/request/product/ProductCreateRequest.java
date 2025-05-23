package org.demo.baoleme.dto.request.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductCreateRequest {
    @NotNull(message = "店铺ID不能为空")
    private Long storeId;

    @NotNull(message = "商品名不能为空")
    private String name;

    private String description;

    @NotNull(message = "商品价格不能为空")
    private BigDecimal price;

    private String category;

    @NotNull(message = "商品库存不能为空")
    private Integer stock;

    private String image;
}
