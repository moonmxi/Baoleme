package org.demo.baoleme.dto.response.merchant;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data   // 使用了Lombok的@Data注解，所以自动生成了getter和setter方法
// 这意味着字段名在序列化为JSON时会使用蛇形命名法
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MerchantLoginResponse {
    private String token;
    private Long userId;
    private long expiresIn = 1000 * 60 * 60 * 24;
}