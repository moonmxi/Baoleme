package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @TableField("store_location")
    private String storeLocation;

    @TableField("user_location")
    private String userLocation;

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

    @TableField("actual_price")
    private BigDecimal actualPrice;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("deadline")
    private LocalDateTime deadline;

    @TableField("ended_at")
    private LocalDateTime endedAt;

    @TableField("remark")
    private String remark;

    public void setOrderId(Long id) {
        this.id = id;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice; // 注意：这里映射到total_price字段
    }


    public void setRemark(String remark) {
        this.remark = remark;
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