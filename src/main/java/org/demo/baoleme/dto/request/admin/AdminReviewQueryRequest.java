package org.demo.baoleme.dto.request.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminReviewQueryRequest {
    private Long userId;
    private Long storeId;
    private Long productId;
    private BigDecimal startRating;
    private BigDecimal endRating;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer page;
    private Integer pageSize;
}