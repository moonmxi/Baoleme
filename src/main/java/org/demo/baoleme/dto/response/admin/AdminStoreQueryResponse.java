package org.demo.baoleme.dto.response.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminStoreQueryResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
    private BigDecimal rating;
    private BigDecimal balance;
    private int status;
    private LocalDateTime createAt;
    private String image;
}