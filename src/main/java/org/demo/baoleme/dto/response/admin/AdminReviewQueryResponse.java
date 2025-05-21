package org.demo.baoleme.dto.response.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminReviewQueryResponse {
    private Long id;
    private Long userId;
    private Long storeId;
    private Long productId;
    private BigDecimal rating;
    private String comment;
    private LocalDateTime createdAt;
}