package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Order;
import com.example.demo.entity.Shop;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ShopRepository;
import com.example.demo.service.OrderStatsService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MerchantController {

    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;
    private final OrderStatsService orderStatsService;

    public MerchantController(
            ShopRepository shopRepository,
            OrderRepository orderRepository,
            OrderStatsService orderStatsService
    ) {
        this.shopRepository = shopRepository;
        this.orderRepository = orderRepository;
        this.orderStatsService = orderStatsService;
    }

    @GetMapping("/merchant")
    public String merchant(Model model, HttpSession session) {
        // 从 Session 中获取用户名
        String username = (String) session.getAttribute("username");
        // 将用户名传递给模板
        model.addAttribute("username", username);

        // 创建Shop对象代替Map
        Shop shop = shopRepository.findByMerchantName(username);
        // 添加至Model
        model.addAttribute("shop", shop);

        List<Order> orders = (shop != null)
                ? orderRepository.findByStoreId(shop.getId())
                : Collections.emptyList();
        model.addAttribute("orders", orders);

        if (shop != null) {
            // 设置时间范围（默认最近30天）
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusDays(30);

            // 获取统计信息
            Map<String, Object> stats = orderStatsService.getCompletedOrderStats(
                    shop.getId(),
                    start,
                    end
            );

            // 将统计结果加入模型
            model.addAttribute("stats", stats);
        }

        return "merchant";
    }

}
