package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("favorite")  // 指定数据库表名
public class Favorite {
    @TableId(type = IdType.AUTO)  // 主键自增
    private Long id;

    private Long userId;  // 用户ID外键，替代@ManyToOne private User user

    private Long storeId;  // 店铺ID外键，替代@ManyToOne private Store store

    private LocalDateTime createdAt;
}