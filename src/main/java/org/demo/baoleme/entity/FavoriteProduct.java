package org.demo.baoleme.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

@TableName("favorite_products")  // 指定数据库表名
public class FavoriteProduct {
    @TableId(type = IdType.AUTO)  // 主键自增
    private Long id;

    private Long userId;  // 使用外键ID代替 @ManyToOne

    private Long productId;  // 使用外键ID代替 @ManyToOne

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}