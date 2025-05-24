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
@TableName("`order`")
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
     * 订单状态
     * 0：待接单  1：准备中  2：配送中  3：完成  4：取消
     */
    @TableField("status")
    private Integer status;

    @TableField("user_location")
    private String userLocation;

    @TableField("store_location")
    private String storeLocation;

    @TableField("total_price")
    private BigDecimal totalPrice;

    @TableField("actual_price")
    private BigDecimal actualPrice;

    @TableField("delivery_price")
    private BigDecimal deliveryPrice;

    //备注
    @TableField("remark")
    private String remark;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("deadline")
    private LocalDateTime deadline;

    @TableField("ended_at")
    private LocalDateTime endedAt;
}