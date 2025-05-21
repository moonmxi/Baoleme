package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("cart")
public class Cart {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Date createdAt;
}