package org.demo.baoleme.dto.response.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserCurrentOrderResponse {
    private Long orderId;
    private LocalDateTime createdAt;
    private Integer status;
    private String remark;
    @TableField("user_location")
    private String userLocation;
    @TableField("store_location")
    private String storeLocation;
    @TableField("total_price")
    private BigDecimal totalPrice;
    @TableField("actual_price")
    private BigDecimal actualPrice;
    @TableField("delivery_price")
    private BigDecimal deliveryPrice;
    private String storeName;
    private String riderName;
    private String riderPhone;
    private String storePhone;
}