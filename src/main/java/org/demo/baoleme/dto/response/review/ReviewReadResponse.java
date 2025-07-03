package org.demo.baoleme.dto.response.review;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReviewReadResponse {
    private Long userId;
    private String username;
    private Long productId;
    private String productName;
    private BigDecimal rating;
    private String comment;
    private LocalDateTime createdAt;
    private String userAvatar;
    private String image;
}
