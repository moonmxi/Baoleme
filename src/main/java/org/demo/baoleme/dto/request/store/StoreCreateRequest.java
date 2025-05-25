package org.demo.baoleme.dto.request.store;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StoreCreateRequest {
    @NotBlank(message = "店铺名不能为空")
    private String name;

    private String description;
    private String location;
    private String image;
    private String type;
}
