package org.demo.baoleme.dto.response.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserFavoriteResponse {
    private Long storeId;
    private String name;
    private String description;
    private String location;
    private String type;
    /**
     * 评分（decimal(2,1), 默认5.0）
     */
    private BigDecimal rating = BigDecimal.valueOf(5.0);
    /**
     * 状态（1-开启，0-关闭）
     */
    private Integer status;
    private LocalDateTime createdAt;
    private String image;
}
