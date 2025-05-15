package com.example.demo.controller;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Merchant;
import com.example.demo.service.MerchantService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    private final MerchantService merchantService;

    @Autowired
    public HomeController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    // 显示编辑资料页面
    @GetMapping("/merchant/edit-profile")
    public String showEditProfile(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/merchant/login";
        }

        Merchant merchant = merchantService.getMerchantByUsername(username);
        model.addAttribute("merchant", merchant);
        return "edit-profile";
    }

    // 处理资料修改提交
    @PostMapping("/merchant/update-profile")
    public String updateProfile(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            @RequestParam(required = false) String phone,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        // 转换空字符串为null
        if (phone != null && phone.isEmpty()) {
            phone = null;
        }

        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername == null) {
            return "redirect:/merchant/login";
        }

        Merchant currentMerchant = merchantService.getMerchantByUsername(currentUsername);

        // 1. 检查新用户名是否已存在（且不是当前用户）
        if (!currentUsername.equals(username)) {
            Merchant existingMerchant = merchantService.getMerchantByUsername(username);
            if (existingMerchant != null) {
                redirectAttributes.addFlashAttribute("usernameError", "用户名已被占用");
                return "redirect:/merchant/edit-profile";
            }
            currentMerchant.setUsername(username);
        }

        // 密码修改逻辑
        if (!newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("passwordError", "两次输入的密码不一致");
                return "redirect:/merchant/edit-profile";
            }
            currentMerchant.setPassword(newPassword); // 实际应加密存储
        }

        // 电话格式验证（示例）
        if (phone != null && !phone.matches("^\\d{5,15}$")) {
            redirectAttributes.addFlashAttribute("phoneError", "联系电话格式无效");
            return "redirect:/merchant/edit-profile";
        }
        currentMerchant.setPhone(phone);

        merchantService.updateMerchant(currentMerchant);
        session.setAttribute("username", username); // 更新Session中的用户名
        redirectAttributes.addFlashAttribute("successMessage", "资料修改成功！");
        return "redirect:/merchant#personal";
    }

    // 账户注销（删除账户）
    @PostMapping("/merchant/delete-account")
    public String deleteAccount(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/merchant/login"; // 未登录用户重定向到登录页
        }

        // 1. 查询用户信息
        Merchant merchant = merchantService.getMerchantByUsername(username);
        if (merchant == null) {
            return "redirect:/merchant/login?error=not_found";
        }

        // 2. 执行删除操作
        merchantService.deleteMerchant(merchant.getId());

        // 3. 销毁会话并跳转到注销成功页
        session.invalidate();
        return "redirect:/merchant/account-deleted";
    }

// 注销成功页
    @GetMapping("/merchant/account-deleted")
    public String accountDeleted() {
        return "account-deleted"; // 对应 templates/account-deleted.html
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

        Merchant m = merchantService.getMerchantByUsername(username); // 需要先在Mapper接口添加该方法
        if (m == null || !m.isPasswordAccept(password)) {
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
            @RequestParam String password,
            @RequestParam(required = false) String phone // 可选参数
    ) {
        // 在 VSCode 终端打印注册信息
        System.out.println("注册信息 - 用户名: " + username + " 密码: " + password);

        // 输入验证
        if (!Merchant.validateCredentials(username, password)) {
            return "redirect:/merchant/register?error=invalid";
        }

        // 检查用户名是否已存在
        if (merchantService.getMerchantByUsername(username) != null) {
            return "redirect:/merchant/register?error=exists";
        }

        // 构建Merchant对象
        Merchant merchant = new Merchant();
        merchant.setUsername(username);
        merchant.setPassword(password);
        merchant.setPhone(phone); // 可为null
        merchant.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // 调用Service保存到数据库
        try {
            merchantService.createMerchant(merchant);
            return "redirect:/merchant/regist-success";
        } catch (Exception e) {
            return "redirect:/merchant/register?error=database";
        }
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
