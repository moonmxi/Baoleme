package org.demo.baoleme.dto.request.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminReviewQueryRequest {
    private Long user_id;
    private Long store_id;
    private Long product_id;
    private Integer rating;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private Integer page;
    private Integer page_size;
}