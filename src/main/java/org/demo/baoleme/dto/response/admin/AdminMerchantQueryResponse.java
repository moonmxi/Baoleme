package org.demo.baoleme.dto.response.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminMerchantQueryResponse {
    private Long id;
    private String username;
    private String phone;
    private String avatar;
    private LocalDateTime createdAt;
}