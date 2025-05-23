package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.demo.baoleme.pojo.Order;
import org.demo.baoleme.pojo.OrderItem;

public interface OrderItemMapper extends BaseMapper<OrderItem> {
    @Insert("INSERT INTO order_item (order_id, product_id, quantity) " +
            "VALUES (#{orderId}, #{productId}, #{quantity})")
    int insert(OrderItem orderItem);
}
