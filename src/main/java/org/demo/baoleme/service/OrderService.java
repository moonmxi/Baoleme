package org.demo.baoleme.service;

import org.demo.baoleme.pojo.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {

    /**
     * 查询可抢订单
     */
    List<Order> getAvailableOrders(int page, int pageSize);

    /**
     * 骑手尝试抢单（并发安全）
     */
    boolean grabOrder(Long orderId, Long riderId);

    /**
     * 骑手取消已接订单
     */
    boolean riderCancelOrder(Long orderId, Long riderId);

    /**
     * 骑手更新订单状态
     */
    boolean riderUpdateOrderStatus(Long orderId, Long riderId, Integer targetStatus);

    /**
     * 查询骑手接单历史
     */
    List<Order> getRiderOrders(Long riderId, Integer status, String startTime, String endTime, int page, int pageSize);

    /**
     * 查询骑手收入统计
     */
    Map<String, Object> getRiderEarnings(Long riderId);

    /**
     * 查询单个订单
     */
    Order getOrderById(Long orderId);
}