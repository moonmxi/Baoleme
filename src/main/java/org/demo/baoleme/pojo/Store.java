package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * unique: id, name
 * not null: id, merchant_id, name
 */
@Data
@TableName("store")
public class Store {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("merchant_id")
    private Long merchantId;

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
    private Integer status = 0;

    private LocalDateTime createdAt;

    private String image;
}