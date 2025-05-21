package org.demo.baoleme.dto.response.merchant;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MerchantRegisterResponse {
    // response 的 data 部分
    private Long userId;
    private String username;
    private String phone;
}