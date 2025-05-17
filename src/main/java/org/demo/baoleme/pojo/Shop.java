package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("store")
public class Shop {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("merchant_id")
    private Long merchantId;

    private String name;

    private String type;

    private String location;

    /**
     * 评分（decimal(2,1), 默认5.0）
     */
    private BigDecimal rating = BigDecimal.valueOf(5.0);

    /**
     * 余额（decimal(10,2), 默认0.0）
     */
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 状态（1-开启，0-关闭）
     */
    private ShopStatus status = ShopStatus.ENABLED;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 店铺状态枚举（与数据库tinyint映射）
     */
    public enum ShopStatus {
        ENABLED(1), DISABLED(0);

        @EnumValue  // MyBatis-Plus 枚举注解
        private final int code;

        ShopStatus(int code) {
            this.code = code;
        }
    }
}