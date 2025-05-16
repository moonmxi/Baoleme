package org.demo.baoleme.controller;

import jakarta.validation.Valid;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.dto.request.OrderCancelRequest;
import org.demo.baoleme.dto.request.OrderGrabRequest;
import org.demo.baoleme.dto.request.OrderStatusRiderUpdateRequest;
import org.demo.baoleme.dto.request.RiderOrderHistoryQueryRequest;
import org.demo.baoleme.dto.response.OrderGrabResponse;
import org.demo.baoleme.dto.response.OrderStatusRiderUpdateResponse;
import org.demo.baoleme.dto.response.RiderEarningsResponse;
import org.demo.baoleme.dto.response.RiderOrderHistoryResponse;
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
        boolean ok = orderService.grabOrder(request.getOrder_id(), riderId);
        if (!ok) {
            return ResponseBuilder.fail("订单已被抢或不存在");
        }

        OrderGrabResponse response = new OrderGrabResponse();
        response.setOrder_id(request.getOrder_id());
        response.setPickup_deadline(LocalDateTime.now().plusMinutes(30)); // 假设30分钟取货

        return ResponseBuilder.ok(response);
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel")
    public CommonResponse cancelOrder(@Valid @RequestBody OrderCancelRequest request) {
        Long riderId = UserHolder.getId();
        boolean ok = orderService.cancelOrder(request.getOrder_id(), riderId);
        if (!ok) {
            return ResponseBuilder.fail("当前状态不可取消或订单不存在");
        }
        return ResponseBuilder.ok(Map.of(
                "order_id", request.getOrder_id(),
                "status", "CANCELLED"
        ));
    }

    /**
     * 更新订单状态
     */
    @PostMapping("/rider-update-status")
    public CommonResponse updateOrderStatus(@Valid @RequestBody OrderStatusRiderUpdateRequest request) {
        Long riderId = UserHolder.getId();
        boolean ok = orderService.riderUpdateOrderStatus(request.getOrder_id(), riderId, request.getTarget_status());
        if (!ok) {
            return ResponseBuilder.fail("订单状态更新失败");
        }

        OrderStatusRiderUpdateResponse response = new OrderStatusRiderUpdateResponse();
        response.setOrder_id(request.getOrder_id());
        response.setStatus(request.getTarget_status());
        response.setUpdated_at(request.getTimestamp());

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
                request.getStart_time(),
                request.getEnd_time(),
                request.getPage(),
                request.getPage_size()
        );

        List<RiderOrderHistoryResponse> responses = orders.stream().map(order -> {
            RiderOrderHistoryResponse resp = new RiderOrderHistoryResponse();
            resp.setOrder_id(order.getId());
            resp.setStatus(order.getStatus());
            resp.setTotal_amount(order.getTotalPrice());
            resp.setCompleted_at(order.getEndedAt());
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("orders", responses));
    }

    /**
     * 查询收入统计
     */
    @GetMapping("/earnings")
    public CommonResponse getRiderEarnings() {
        Long riderId = UserHolder.getId();
        Map<String, Object> result = orderService.getRiderEarnings(riderId);

        RiderEarningsResponse response = new RiderEarningsResponse();
        response.setCompleted_orders(((Number) result.getOrDefault("completed_orders", 0)).intValue());
        response.setTotal_earnings((BigDecimal) result.getOrDefault("total_earnings", BigDecimal.ZERO));
        response.setCurrent_month((BigDecimal) result.getOrDefault("current_month", BigDecimal.ZERO));

        return ResponseBuilder.ok(response);
    }
}