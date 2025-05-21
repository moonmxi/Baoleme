package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 订单实体
 */
@Data
@TableName("order")
public class Order {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("store_id")
    private Long storeId;

    @TableField("address")
    private String address;

    @TableField("rider_id")
    private Long riderId;

    /**
     * 订单状态
     * 0：待接单  1：准备中  2：配送中  3：完成  4：取消
     */
    @TableField("status")
    private Integer status;

    @TableField("total_price")
    private BigDecimal totalPrice;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("deadline")
    private LocalDateTime deadline;

    @TableField("ended_at")
    private LocalDateTime endedAt;

    @TableField("remark")
    private String remark;

    // 订单编号生成器（示例用简单实现，实际生产环境建议用更健壮的方案）
    private static final AtomicLong ORDER_NUMBER_SEQUENCE = new AtomicLong(1000);

    /**
     * 生成订单编号（格式：ORD+时间戳+序列号）
     */
    public static String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + ORDER_NUMBER_SEQUENCE.getAndIncrement();
    }

    /**
     * 生成订单ID（由MyBatis-Plus自动生成，此方法仅作备用）
     */
    public static Long generateOrderId() {
        // 实际生产中使用MyBatis-Plus的IdType.ASSIGN_ID策略即可
        // 这里只是示例，不建议在生产中使用
        return System.currentTimeMillis() + ORDER_NUMBER_SEQUENCE.getAndIncrement();
    }


    public void setOrderId(Long id) {
        this.id = id;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice; // 注意：这里映射到total_price字段
    }


    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getOrderId() {
        return this.id;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice; // 注意：这里返回total_price字段
    }


    // 其他可能需要的方法
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Integer getStatus() {
        return this.status;
    }

}