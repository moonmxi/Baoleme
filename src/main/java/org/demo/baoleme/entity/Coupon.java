package org.demo.baoleme.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;


import java.time.LocalDate;

@TableName("coupons")  // 指定表名（如果表名和类名一致，可以省略）
public class Coupon {
    @TableId(type = IdType.AUTO)  // 主键自增（对应数据库的 AUTO_INCREMENT）
    private Long id;

    private String code;
    private Integer discount;
    private LocalDate expirationDate;

    // getters and setters
}