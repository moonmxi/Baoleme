package org.demo.baoleme.dto.response.merchant;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MerchantUpdateResponse {
    private Long userId;
    private String username;
    private String phone;
    private String avatar;
    private String newToken;
}
