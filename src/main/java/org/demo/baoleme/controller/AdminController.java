package org.demo.baoleme.controller;

import jakarta.validation.Valid;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.AdminLoginRequest;
import org.demo.baoleme.dto.response.AdminLoginResponse;
import org.demo.baoleme.pojo.Admin;
import org.demo.baoleme.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public AdminController(AdminService adminService, RedisTemplate<String, Object> redisTemplate) {
        this.adminService = adminService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public CommonResponse login(@Valid @RequestBody AdminLoginRequest request) {
        Admin admin = adminService.login(request.getId(), request.getPassword());
        if (admin == null) {
            return ResponseBuilder.fail("账号或密码错误");
        }

        String token = JwtUtils.createToken(admin.getId(), "admin", null);
        String redisKey = "admin:token:" + token;
        redisTemplate.opsForValue().set(redisKey, admin.getId(), 1, TimeUnit.DAYS); // 有效期1天

        AdminLoginResponse response = new AdminLoginResponse();
        response.setId(admin.getId());
        response.setToken(token);

        return ResponseBuilder.ok(response);
    }

    /**
     * 管理员登出
     */
    @PostMapping("/logout")
    public CommonResponse logout(@RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        redisTemplate.delete("admin:token:" + token);

        return ResponseBuilder.ok("登出成功");
    }

}