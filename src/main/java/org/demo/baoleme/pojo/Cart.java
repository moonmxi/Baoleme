package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("cart") // 映射数据库中的 cart 表
public class Cart {

    @TableId("user_id") // 指定主键字段（如果有联合主键，只能标记一个主键字段，另一个不加注解）
    private Long userId;

    @TableField("product_id")
    private Long productId;

    @TableField("quantity")
    private Integer quantity;

    @TableField("create_time")
    private Date createTime;
}