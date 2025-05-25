package org.demo.baoleme.controller;

import jakarta.validation.Valid;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.dto.request.merchant.*;
import org.demo.baoleme.dto.response.merchant.*;
import org.demo.baoleme.pojo.Merchant;
import org.demo.baoleme.service.MerchantService;
import org.demo.baoleme.common.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    private final MerchantService merchantService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping("/register")
    public CommonResponse register(
            @RequestBody @Valid MerchantRegisterRequest request
    ) {
        System.out.println("收到注册请求: " + request);

        // Step1: 创建Merchant对象并拷贝属性
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(request, merchant);

        // Step2: 调用Service层创建商家
        Merchant result = merchantService.createMerchant(merchant);

        // Step3: 处理创建结果
        if (result == null) {
            return ResponseBuilder.fail("注册失败：用户名或手机号重复");
        }

        // Step4: 构建响应体
        MerchantRegisterResponse response = new MerchantRegisterResponse();
        response.setUserId(result.getId());
        response.setUsername(result.getUsername());
        response.setPhone(result.getPhone());

        System.out.println("注册成功，响应: " + ResponseBuilder.ok(response));
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/login")
    public CommonResponse login(
            @RequestBody @Valid MerchantLoginRequest request
    ) {
        System.out.println("收到登录请求: " + request);

        // Step1: 根据手机号查询商家
        Merchant result = merchantService.getMerchantByPhone(request.getPhone());

        // Step2: 验证用户存在性
        if (result == null) {
            return ResponseBuilder.fail("手机号不存在");
        }

        // Step3: 验证密码匹配
        // 使用 BCrypt 验证密码
        // 第一个参数是明文密码（未加密），第二个参数是加密后的密码哈希
        if (!passwordEncoder.matches(request.getPassword(), result.getPassword())) {
            return ResponseBuilder.fail("密码错误");
        }

        String loginKey = "merchant:login:" + result.getId();
        if (redisTemplate.hasKey(loginKey)) {
            return ResponseBuilder.fail("该商家已登录，请先登出");
        }

        // Step4: 生成JWT令牌
        String token = JwtUtils.createToken(result.getId(), "merchant", result.getUsername());
        redisTemplate.opsForValue().set("merchant:token:" + token, result.getId(), 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(loginKey, token, 1, TimeUnit.DAYS);

        // Step5: 构建登录响应
        MerchantLoginResponse response = new MerchantLoginResponse();
        response.setToken(token);
        response.setUserId(result.getId());

        System.out.println("登录成功，响应: " + response);
        return ResponseBuilder.ok(response);
    }

    @GetMapping("/info")
    public CommonResponse getInfo(@RequestHeader("Authorization") String tokenHeader) {
        System.out.println("收到获取信息请求");

        // Step1: 从线程上下文获取用户ID
        Long id = UserHolder.getId();

        // Step2: 查询商家详细信息
        Merchant merchant = merchantService.getMerchantById(id);

        // Step3: 验证查询结果
        if (merchant == null) {
            return ResponseBuilder.fail("当前身份无效或用户不存在");
        }

        // Step4: 构建信息响应体
        MerchantInfoResponse response = new MerchantInfoResponse();
        BeanUtils.copyProperties(merchant, response);
        response.setUserId(merchant.getId());

        System.out.println("获取信息成功，响应: " + response);
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/update")
    public CommonResponse update(@RequestBody MerchantUpdateRequest request,
                                 @RequestHeader("Authorization") String tokenHeader) {
        System.out.println("收到更新请求: " + request);

        // Step1: 创建Merchant对象并设置用户ID
        Merchant newMerchant = new Merchant();
        Long id = UserHolder.getId();
        newMerchant.setId(id);

        String oldMerchantName = merchantService.getMerchantById(id).getUsername();

        // Step2: 拷贝请求参数到实体对象
        BeanUtils.copyProperties(request, newMerchant);

        // Step3: 调用Service执行更新操作
        Merchant afterUpdate = merchantService.updateInfo(newMerchant);

        // Step4: 处理更新失败情况
        if (afterUpdate == null) {
            return ResponseBuilder.fail("更新失败，请检查字段");
        }

        // Step5: 构建基础响应体
        MerchantUpdateResponse response = new MerchantUpdateResponse();
        response.setUsername(request.getUsername());
        response.setUserId(id);
        response.setPhone(afterUpdate.getPhone());
        response.setAvatar(afterUpdate.getAvatar());

        // Step6: 检查用户名是否发生变更
        boolean usernameChanged = request.getUsername() != null
                && !oldMerchantName.equals(afterUpdate.getUsername());
        if (!usernameChanged) {
            return ResponseBuilder.ok(response);  // 用户名未修改直接返回
        }

        // Step7: 清理旧Token相关缓存
        String oldToken = tokenHeader.replace("Bearer ", "");
        redisTemplate.delete("merchant:token:" + oldToken);
        redisTemplate.delete("merchant:login:" + id);

        // Step8: 生成新Token并更新缓存
        String newToken = JwtUtils.createToken(id, "merchant", request.getUsername());
        redisTemplate.opsForValue().set("merchant:token:" + newToken, id, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set("merchant:login:" + id, newToken, 1, TimeUnit.DAYS);

        // Step9: 设置响应中的新Token
        response.setNewToken(newToken);

        System.out.println("更新成功，响应: " + response);
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/logout")
    public CommonResponse logout(@RequestHeader("Authorization") String tokenHeader) {
        System.out.println("收到登出请求，Token: " + tokenHeader);

        removeToken(tokenHeader);

        // Step4: 返回操作结果
        return ResponseBuilder.ok();
    }

    @DeleteMapping("/delete")
    public CommonResponse delete(@RequestHeader("Authorization") String tokenHeader) {
        System.out.println("收到删除请求");

        // Step1: 获取当前认证用户ID
        Long id = UserHolder.getId();

        // Step2: 执行删除操作
        boolean ok = merchantService.deleteMerchant(id);

        removeToken(tokenHeader);

        // Step3: 返回操作结果
        return ok ? ResponseBuilder.ok() : ResponseBuilder.fail("注销失败");
    }

    private void removeToken(String tokenHeader) {
        // Step1: 解析并提取原始Token
        String token = tokenHeader.replace("Bearer ", "");
        String tokenKey = "merchant:token:" + token;

        // Step2: 获取并删除关联的登录状态
        Object merchantId = redisTemplate.opsForValue().get(tokenKey);
        if (merchantId != null) {
            String loginKey = "merchant:login:" + merchantId;
            redisTemplate.delete(loginKey);
        }

        // Step3: 删除Token本体
        redisTemplate.delete(tokenKey);
    }
}