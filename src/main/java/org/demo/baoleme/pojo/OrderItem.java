package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import java.math.BigDecimal;
import lombok.Data;
@Data
@TableName("order_item")
public class OrderItem {

    private Long orderId;

    private Long productId;

    private Integer quantity;


}