package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long storeId;

    private String name;

    private String description;

    private BigDecimal price;

    private String category;

    private int stock;

    private BigDecimal rating;

    private int status;

    private LocalDateTime createdAt;

    private String image;
}
