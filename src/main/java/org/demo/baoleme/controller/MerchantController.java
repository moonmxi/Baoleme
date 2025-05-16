package org.demo.baoleme.controller;

import ch.qos.logback.core.boolex.Matcher;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.dto.request.*;
import org.demo.baoleme.dto.response.*;
import org.demo.baoleme.pojo.Merchant;
import org.demo.baoleme.service.MerchantService;
import org.demo.baoleme.common.*;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    private final MerchantService merchantService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping("/register")
    public CommonResponse register(@RequestBody MerchantRegisterRequest request) {
        System.out.println("收到注册请求: " + request);
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(request, merchant);
        Merchant result = merchantService.createMerchant(merchant);
        if (result == null) {
            return ResponseBuilder.fail("注册失败：用户名或手机号重复");
        }

        MerchantRegisterResponse response = new MerchantRegisterResponse();
        response.setUserId(result.getId());
        response.setUsername(result.getUsername());
        response.setPhone(result.getPhone());
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/login")
    public CommonResponse login(@RequestBody MerchantLoginRequest request) {
        Merchant result = merchantService.getMerchantByUsername(request.getUsername());

        if (result == null) {
            return ResponseBuilder.fail("用户名不存在");
        }

        // 使用 BCrypt 验证密码
        if (!passwordEncoder.matches(request.getPassword(), result.getPassword())) {
            return ResponseBuilder.fail("密码错误");
        }

        String token = JwtUtils.createToken(result.getId(), "merchant", result.getUsername());
        MerchantLoginResponse response = new MerchantLoginResponse();
        response.setToken(token);
        response.setUsername(result.getUsername());
        response.setUserId(result.getId());
        return ResponseBuilder.ok(response);
    }

    @GetMapping("/info")
    public CommonResponse getInfo() {
        Long id = UserHolder.getId();
        Merchant merchant = merchantService.getMerchantById(id);
        if (merchant == null) {
            return ResponseBuilder.fail("当前身份无效或用户不存在");
        }

        MerchantInfoResponse response = new MerchantInfoResponse();
        BeanUtils.copyProperties(merchant, response);
        response.setUserId(merchant.getId());
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/update")
    public CommonResponse update(@RequestBody MerchantUpdateRequest request) {
        Merchant merchant = new Merchant();
        merchant.setId(UserHolder.getId());
        BeanUtils.copyProperties(request, merchant);
        Merchant m = merchantService.updateMerchant(merchant);
        boolean success = (m != null);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("更新失败，请检查请求字段");
    }

    @DeleteMapping("/delete")
    public CommonResponse delete() {
        boolean ok = merchantService.deleteMerchant(UserHolder.getId());
        return ok ? ResponseBuilder.ok() : ResponseBuilder.fail("注销失败");
    }
}