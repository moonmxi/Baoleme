package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("coupon")
public class Coupon {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private String desc;

    private LocalDate expirationDate;  // 对应数据库 expiration_date

    private LocalDateTime createdAt;   // 对应数据库 created_at（自动填充需额外配置）

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private CouponType type;

    private BigDecimal discount;

    private int fullAmount;

    private int reduceAmount;

    public enum CouponType {
        DISCOUNT("DISCOUNT", "折扣"),
        FULL_REDUCE("FULL_REDUCE", "满减");

        private final String code;  // 对应数据库的VARCHAR值
        private final String desc;  // 中文描述

        CouponType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}