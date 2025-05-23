package org.demo.baoleme.dto.response.merchant;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MerchantInfoResponse {
    private Long userId;
    private String username;
    private String phone;
    private LocalDateTime createdAt;
    private String avatar;
}