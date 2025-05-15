package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Merchant;
import com.example.demo.mapper.MerchantMapper;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    private final MerchantMapper merchantMapper;

    @Autowired
    public HomeController(MerchantMapper merchantMapper) {
        this.merchantMapper = merchantMapper;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @PostMapping("/merchant/logout")
    public String logout(HttpSession session) {
        String username = (String) session.getAttribute("username");
        System.out.println("登出信息 - 用户名: " + username);

        session.invalidate(); // 销毁当前会话
        return "redirect:/merchant/login?logout"; // 重定向到登录页
    }

    // 新增商家登录路由
    @GetMapping("/merchant/login")
    public String merchantLogin() {
        System.out.println("=== 当前商户表内容 ===");
        List<Merchant> merchants = merchantMapper.selectAll();
        merchants.forEach(merchant -> {
            System.out.printf(
                    "ID: %d | 用户名: %-15s | 密码: %-15s | 电话: %-15s | 创建时间: %s%n",
                    merchant.getId(),
                    merchant.getUsername(),
                    merchant.getPassword(),
                    merchant.getPhone() != null ? merchant.getPhone() : "N/A",
                    merchant.getCreatedAt()
            );
        });
        System.out.println("====================");

        return "merchant-login"; // 对应 templates/merchant-login.html
    }

    @PostMapping("/merchant/login")
    public String handleLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session
    ) {
        // 在 VSCode 终端打印注册信息
        System.out.println("登录信息 - 用户名: " + username + " 密码: " + password);

        Merchant m = merchantMapper.selectByUsername(username); // 需要先在Mapper接口添加该方法
        if (!m.isPasswordAccept(password)) {
            return "redirect:/merchant/login?error";
        }

        // 将用户名存入 Session
        session.setAttribute("username", username);

        // 跳转到成功页面（需要你自行实现 regist-success 的映射）
        return "redirect:/merchant#personal";
    }

    // 新增商家注册路由
    @GetMapping("/merchant/register")
    public String merchantRegister() {
        return "merchant-regist";
    }

    // 新增 POST 请求处理（接收表单数据）
    @PostMapping("/merchant/register")
    public String handleRegistration(
            @RequestParam String username,
            @RequestParam String password
    ) {
        // 在 VSCode 终端打印注册信息
        System.out.println("注册信息 - 用户名: " + username + " 密码: " + password);

        if (!Merchant.validateCredentials(username, password)) {
            return "redirect:/merchant/register?error";
        }

        // 跳转到成功页面（需要你自行实现 regist-success 的映射）
        return "regist-success";
    }

    // 新增注册success路由
    @GetMapping("/merchant/regist-success")
    public String merchantRegistSuccess() {
        return "regist-success";
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello-page";
    }
}
