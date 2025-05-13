package com.example.demo.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.mapper.OrderRepository;
import com.example.demo.mapper.ShopRepository;
import com.example.demo.pojo.Order;
import com.example.demo.pojo.Shop;

import jakarta.servlet.http.HttpSession;

@Controller
public class MerchantController {

    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;

    public MerchantController(ShopRepository shopRepository, OrderRepository orderRepository) {
        this.shopRepository = shopRepository;
        this.orderRepository = orderRepository;
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

        List<Order> orders = (shop != null) ? 
            orderRepository.findByStoreId(shop.getId()) : 
            Collections.emptyList();
        model.addAttribute("orders", orders);

        return "merchant";
    }

}
