package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("store_id")
    private Long storeId;

    private String name;

    private String description;

    private BigDecimal price;

    private String category;

    private Integer stock;

    private BigDecimal rating;

    /**
     * 商品状态（1-上架，0-下架）
     */
    private int status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

}