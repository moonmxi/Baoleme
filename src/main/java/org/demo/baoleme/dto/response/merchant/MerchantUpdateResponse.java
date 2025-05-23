package org.demo.baoleme.dto.response.merchant;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MerchantUpdateResponse {
    private Long userId;
    private String username;
    private String phone;
    private String avatar;
    private String token;
}
