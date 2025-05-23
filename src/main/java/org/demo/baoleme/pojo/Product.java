package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * unique: id
 * not null: id, store_id, name, price, stock
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long storeId;

    private String name;

    private String description;

    private BigDecimal price;

    private String category;

    private Integer stock;

    private BigDecimal rating;

    private Integer status;

    private LocalDateTime createdAt;

    private String image;
}
