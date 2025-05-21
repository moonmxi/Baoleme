package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
@Data
@TableName("favorite")  // 指定数据库表名
public class Favorite {
    @TableId(type = IdType.AUTO)  // 主键自增
    private Long id;

    private Long userId;  // 用户ID外键，替代@ManyToOne private User user

    private Long storeId;  // 店铺ID外键，替代@ManyToOne private Store store

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

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "FavoriteStore{" +
                "id=" + id +
                ", userId=" + userId +
                ", storeId=" + storeId +
                '}';
    }
}