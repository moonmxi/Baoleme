package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;


import java.security.Timestamp;
import java.util.Date;

@TableName("coupons")  // 指定表名（如果表名和类名一致，可以省略）
public class Coupon {
    @TableId(type = IdType.AUTO)  // 主键自增（对应数据库的 AUTO_INCREMENT）
    private Long id;
    private Long userId;
    private Long merchantId;
    private double discount;
    private Date expirationDate;
    private Timestamp createdAt;

    // getters and setters
}