package org.demo.baoleme.dto.request.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderCreateRequest {

    // 商品列表，每个商品带有数量
    private List<CartItemDTO> items;

    private Long couponId;       // 可选优惠券 ID

    private BigDecimal deliveryPrice; // 配送费

    private Long storeId;        // 下单的商家 ID

    private String remark;       // 用户备注

    private LocalDateTime deadline;

    private String userLocation;
}
