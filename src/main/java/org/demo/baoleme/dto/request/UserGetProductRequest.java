package org.demo.baoleme.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserGetProductRequest {
    private Long shopId;
    private String category;
    private float minPrice;
    private float maxPrice;
    private String sortBy;
}
