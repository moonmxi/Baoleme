package org.demo.baoleme.controller;

import jakarta.validation.Valid;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.dto.request.rider.RiderDispatchModeRequest;
import org.demo.baoleme.dto.request.rider.RiderLoginRequest;
import org.demo.baoleme.dto.request.rider.RiderRegisterRequest;
import org.demo.baoleme.dto.request.rider.RiderUpdateRequest;
import org.demo.baoleme.dto.response.rider.RiderDispatchModeResponse;
import org.demo.baoleme.dto.response.rider.RiderInfoResponse;
import org.demo.baoleme.dto.response.rider.RiderLoginResponse;
import org.demo.baoleme.dto.response.rider.RiderRegisterResponse;
import org.demo.baoleme.pojo.Rider;
import org.demo.baoleme.service.RiderService;
import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.common.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/rider")
public class RiderController {

    private final RiderService riderService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public RiderController(RiderService riderService) {
        this.riderService = riderService;
    }

    @PostMapping("/register")
    public CommonResponse register(@Valid @RequestBody RiderRegisterRequest request) {
        System.out.println("收到注册请求: " + request);
        Rider rider = new Rider();
        BeanUtils.copyProperties(request, rider);
        Rider result = riderService.register(rider);
        if (result == null) {
            return ResponseBuilder.fail("注册失败：用户名或手机号重复");
        }

        RiderRegisterResponse response = new RiderRegisterResponse();
        response.setUserId(result.getId());
        response.setUsername(result.getUsername());
        response.setPhone(result.getPhone());
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/login")
    public CommonResponse login(@Valid @RequestBody RiderLoginRequest request) {
        Rider result = riderService.login(request.getPhone(), request.getPassword());
        if (result == null) {
            return ResponseBuilder.fail("手机号或密码错误");
        }

        String token = JwtUtils.createToken(result.getId(), "rider", result.getUsername());
        // 将 token 写入 Redis，有效期 1 天
        redisTemplate.opsForValue().set("rider:token:" + token, result.getId(), 1, TimeUnit.DAYS);

        RiderLoginResponse response = new RiderLoginResponse();
        response.setToken(token);
        response.setUsername(result.getUsername());
        response.setUserId(result.getId());
        return ResponseBuilder.ok(response);
    }

    @GetMapping("/info")
    public CommonResponse getInfo() {
        Long id = UserHolder.getId();
        Rider rider = riderService.getInfo(id);
        if (rider == null) {
            return ResponseBuilder.fail("当前身份无效或用户不存在");
        }

        RiderInfoResponse response = new RiderInfoResponse();
        BeanUtils.copyProperties(rider, response);
        response.setUserId(rider.getId());
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/update")
    public CommonResponse update(@Valid @RequestBody RiderUpdateRequest request) {
        Rider rider = new Rider();
        rider.setId(UserHolder.getId());
        BeanUtils.copyProperties(request, rider);
        boolean success = riderService.updateInfo(rider);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("更新失败，请检查请求字段");
    }

    @PatchMapping("/dispatch-mode")
    public CommonResponse switchDispatchMode(@Valid @RequestBody RiderDispatchModeRequest request) {
        Rider rider = new Rider();
        rider.setId(UserHolder.getId());
        rider.setDispatchMode(request.getDispatchMode());

        boolean ok = riderService.updateInfo(rider);
        if (!ok) return ResponseBuilder.fail("切换接单模式失败");

        RiderDispatchModeResponse resp = new RiderDispatchModeResponse();
        resp.setCurrentMode(request.getDispatchMode());
        resp.setModeChangedAt(new Date().toString());
        return ResponseBuilder.ok(resp);
    }

    @PostMapping("/logout")
    public CommonResponse logout(@RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        redisTemplate.delete("rider:token:" + token);

        Long id = UserHolder.getId();
        Rider rider = new Rider();
        rider.setId(id);
        rider.setOrderStatus(-1); // 设置为离线

        boolean success = riderService.updateInfo(rider);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("登出失败");
    }

    @DeleteMapping("/delete")
    public CommonResponse delete() {
        boolean ok = riderService.delete(UserHolder.getId());
        return ok ? ResponseBuilder.ok() : ResponseBuilder.fail("注销失败");
    }
}