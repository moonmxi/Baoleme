package org.demo.baoleme.dto.response.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.demo.baoleme.pojo.Review;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data

public class UserGetProductInfoResponse {
    private Long id;
    private Long storeId;
    private String name;
    private String category;
    private BigDecimal price;
    private String description;
    private String image;
    private Integer stock;
    private BigDecimal rating;
    private Integer status;
    private List<Review> reviews;
    private LocalDateTime createdAt;
}
