package org.demo.baoleme.dto.response.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import lombok.Data;
import org.demo.baoleme.pojo.Order;
import org.demo.baoleme.pojo.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class UserSearchOrderItemResponse {
    private Long orderId;

    private List<OrderItem> items;

}
