package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体类
 */
@Data
@TableName("coupon")
public class Coupon {
    /**
     * 优惠券类型 - 折扣券
     */
    public static final int TYPE_DISCOUNT = 1;

    /**
     * 优惠券类型 - 满减券
     */
    public static final int TYPE_FULL_REDUCTION = 2;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long storeId;
    /**
     * 优惠券类型
     * 1-折扣券 2-满减券
     */
    private Integer type;
    /**折扣率（type=1时有效）*/
    private BigDecimal discount;
    /**满减条件金额（type=2时有效）*/
    private BigDecimal fullAmount;
    /**满减优惠金额（type=2时有效）*/
    private BigDecimal reduceAmount;

    private LocalDateTime expirationDate;
    private LocalDateTime createdAt;
    private Boolean isUsed;

    // 以下是业务方法

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }

    public boolean isValid() {
        return !isExpired() && (isUsed == null || !isUsed);
    }

    /**
     * 应用优惠券计算实际金额
     */
    public BigDecimal applyDiscount(BigDecimal originalAmount) {
        if (!isValid()) {
            return originalAmount;
        }

        if (type == TYPE_DISCOUNT) {
            return originalAmount.multiply(discount);
        } else if (type == TYPE_FULL_REDUCTION) {
            if (originalAmount.compareTo(fullAmount) >= 0) {
                return originalAmount.subtract(reduceAmount);
            }
        }
        return originalAmount;
    }

    // 兼容旧代码的方法
    public int getType() {
        return type != null ? type : 0;
    }

    public BigDecimal getFullAmount() {
        return fullAmount != null ? fullAmount : BigDecimal.ZERO;
    }

    public BigDecimal getReduceAmount() {
        return reduceAmount != null ? reduceAmount : BigDecimal.ZERO;
    }
}