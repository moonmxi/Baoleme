package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`") // 使用反引号转义SQL保留关键字
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("store_id")
    private Long storeId;

    @TableField("rider_id")
    private Long riderId;

    /**
     * 订单状态枚举
     */
    private OrderStatus status = OrderStatus.PENDING;

    @TableField("total_price")
    private BigDecimal totalPrice;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 预计送达时间（需业务层设置，无自动填充）
     */
    private LocalDateTime deadline;

    /**
     * 订单结束时间（可空）
     */
    private LocalDateTime endedAt;

    /**
     * 订单状态枚举定义
     */
    public enum OrderStatus {
        PENDING(0, "等待处理"),
        PREPARING(1, "准备中"),
        DELIVERING(2, "配送中"),
        COMPLETED(3, "已完成"),
        CANCELLED(4, "已取消");

        @EnumValue
        private final int code;
        private final String desc;

        OrderStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}