package org.demo.baoleme.controller;

import jakarta.validation.Valid;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.dto.request.order.OrderCancelRequest;
import org.demo.baoleme.dto.request.order.OrderGrabRequest;
import org.demo.baoleme.dto.request.order.OrderStatusRiderUpdateRequest;
import org.demo.baoleme.dto.request.rider.RiderOrderHistoryQueryRequest;
import org.demo.baoleme.dto.response.order.OrderGrabResponse;
import org.demo.baoleme.dto.response.order.OrderStatusRiderUpdateResponse;
import org.demo.baoleme.dto.response.rider.RiderEarningsResponse;
import org.demo.baoleme.dto.response.rider.RiderOrderHistoryResponse;
import org.demo.baoleme.pojo.Order;
import org.demo.baoleme.service.OrderService;
import org.demo.baoleme.common.UserHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 获取可抢订单列表
     */
    @GetMapping("/available")
    public CommonResponse getAvailableOrders(@RequestParam("page") int page,
                                             @RequestParam("page_size") int pageSize) {
        List<Order> orders = orderService.getAvailableOrders(page, pageSize);
        return ResponseBuilder.ok(Map.of("orders", orders));
    }

    /**
     * 抢单
     */
    @PutMapping("/grab")
    public CommonResponse grabOrder(@Valid @RequestBody OrderGrabRequest request) {
        Long riderId = UserHolder.getId();
        boolean ok = orderService.grabOrder(request.getOrderId(), riderId);
        if (!ok) {
            return ResponseBuilder.fail("订单已被抢或不存在");
        }

        OrderGrabResponse response = new OrderGrabResponse();
        response.setOrderId(request.getOrderId());
        response.setPickupDeadline(LocalDateTime.now().plusMinutes(30)); // 假设30分钟取货

        return ResponseBuilder.ok(response);
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel")
    public CommonResponse cancelOrder(@Valid @RequestBody OrderCancelRequest request) {
        Long riderId = UserHolder.getId();
        boolean ok = orderService.riderCancelOrder(request.getOrderId(), riderId);
        if (!ok) {
            return ResponseBuilder.fail("当前状态不可取消或订单不存在");
        }
        return ResponseBuilder.ok(Map.of(
                "order_id", request.getOrderId(),
                "status", "CANCELLED"
        ));
    }

    /**
     * 更新订单状态
     */
    @PostMapping("/rider-update-status")
    public CommonResponse updateOrderStatus(@Valid @RequestBody OrderStatusRiderUpdateRequest request) {
        Long riderId = UserHolder.getId();
        boolean ok = orderService.riderUpdateOrderStatus(request.getOrderId(), riderId, request.getTargetStatus());
        if (!ok) {
            return ResponseBuilder.fail("订单状态更新失败");
        }

        OrderStatusRiderUpdateResponse response = new OrderStatusRiderUpdateResponse();
        response.setOrderId(request.getOrderId());
        response.setStatus(request.getTargetStatus());
        response.setUpdatedAt(LocalDateTime.now());

        return ResponseBuilder.ok(response);
    }

    /**
     * 查询骑手订单记录
     */
    @PostMapping("/rider-history-query")
    public CommonResponse getRiderOrders(@Valid @RequestBody RiderOrderHistoryQueryRequest request) {
        Long riderId = UserHolder.getId();
        List<Order> orders = orderService.getRiderOrders(
                riderId,
                request.getStatus(),
                request.getStartTime(),
                request.getEndTime(),
                request.getPage(),
                request.getPageSize()
        );

        List<RiderOrderHistoryResponse> responses = orders.stream().map(order -> {
            RiderOrderHistoryResponse resp = new RiderOrderHistoryResponse();
            resp.setOrderId(order.getId());
            resp.setStatus(order.getStatus());
            resp.setTotalAmount(order.getTotalPrice());
            resp.setCompletedAt(order.getEndedAt());
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("orders", responses));
    }

    /**
     * 查询收入统计
     */
    @GetMapping("/rider-earnings")
    public CommonResponse getRiderEarnings() {
        Long riderId = UserHolder.getId();
        Map<String, Object> result = orderService.getRiderEarnings(riderId);

        RiderEarningsResponse response = new RiderEarningsResponse();
        response.setCompletedOrders(((Number) result.getOrDefault("completed_orders", 0)).intValue());
        response.setTotalEarnings((BigDecimal) result.getOrDefault("total_earnings", BigDecimal.ZERO));
        response.setCurrentMonth((BigDecimal) result.getOrDefault("current_month", BigDecimal.ZERO));

        return ResponseBuilder.ok(response);
    }
}