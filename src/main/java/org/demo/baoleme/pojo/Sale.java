package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sales")
public class Sale {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    @TableField("store_id")
    private Long storeId;

    // 就是说创建一个订单要维护两个表
    @TableField("sale_date")
    private LocalDate saleDate;

    private Integer quantity = 1;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    /**
     * 总金额（数据库自动计算，插入/更新时无需手动设置）
     */
    @TableField(value = "total_amount", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private BigDecimal totalAmount;

    @TableField("payment_method")
    private String paymentMethod;

    @TableField("customer_id")
    private Long customerId;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}