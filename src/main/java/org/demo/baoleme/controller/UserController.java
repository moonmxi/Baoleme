package org.demo.baoleme.controller;

import jakarta.validation.Valid;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.order.OrderCreateRequest;
import org.demo.baoleme.dto.request.user.*;
import org.demo.baoleme.dto.response.user.*;
import org.demo.baoleme.pojo.User;
import org.demo.baoleme.service.UserService;
import org.demo.baoleme.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private OrderService orderService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public CommonResponse register(@Valid @RequestBody UserRegisterRequest request) {
        System.out.println("收到注册请求: " + request);
        User user = new User();
        BeanUtils.copyProperties(request, user);
        User result = userService.register(user);

        if (result == null) {
            return ResponseBuilder.fail("注册失败：用户名或手机号已存在");
        }

        UserRegisterResponse response = new UserRegisterResponse();
        response.setUserId(result.getId());
        response.setUsername(result.getUsername());
        response.setPhone(result.getPhone());
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/login")
    public CommonResponse login(@Valid @RequestBody UserLoginRequest request) {
        User result = userService.login(request.getPhone(), request.getPassword());
        if (result == null) {
            return ResponseBuilder.fail("手机号或密码错误");
        }
        String loginKey = "user:login:" + result.getId();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(loginKey))) {
            return ResponseBuilder.fail("该用户已登录，请先登出");
        }
        String token = JwtUtils.createToken(result.getId(), "user", result.getUsername());
        redisTemplate.opsForValue().set("user:token:" + token, result.getId(), 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(loginKey, token, 1, TimeUnit.DAYS);

        UserLoginResponse response = new UserLoginResponse();
        response.setToken(token);
        response.setUsername(result.getUsername());
        response.setUserId(result.getId());
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/logout")
    public CommonResponse logout(@RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        String tokenKey = "user:token:" + token;

        Object userId = redisTemplate.opsForValue().get(tokenKey);
        if (userId != null) {
            String loginKey = "user:login:" + userId;
            redisTemplate.delete(loginKey);       // ✅ 删除登录标识
        }

        redisTemplate.delete(tokenKey);          // ✅ 删除 token 本体

        // 更新用户状态（如果需要）
        Long id = UserHolder.getId();
        User user = new User();
        user.setId(id);
        // 可以设置用户状态为离线或其他状态
        // user.setStatus(0);

        boolean success = userService.updateInfo(user);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("登出失败");
    }
    @GetMapping("/info")
    public CommonResponse getInfo() {
        Long id = UserHolder.getId();
        User user = userService.getInfo(id);
        if (user == null) {
            return ResponseBuilder.fail("用户不存在");
        }

        UserInfoResponse response = new UserInfoResponse();
        BeanUtils.copyProperties(user, response);
        response.setUserId(user.getId());
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/update")
    public CommonResponse update(@Valid @RequestBody UserUpdateRequest request) {
        User user = new User();
        user.setId(UserHolder.getId());
        BeanUtils.copyProperties(request, user);

        boolean success = userService.updateInfo(user);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("更新失败");
    }

    @GetMapping("/history")
    public CommonResponse getOrderHistory() {
        Long userId = UserHolder.getId();
        List<UserOrderHistoryResponse> orders = userService.getOrderHistory(userId);
        return ResponseBuilder.ok(orders);
    }

    @PostMapping("/favorite")
    public CommonResponse favoriteStore(@Valid @RequestBody UserFavoriteRequest request) {
        Long userId = UserHolder.getId();
        boolean success = userService.favoriteStore(userId, request.getStoreId());
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("收藏失败");
    }

    @GetMapping("/favorite/watch")
    public CommonResponse getFavoriteStores() {
        Long userId = UserHolder.getId();

        List<UserFavoriteResponse> stores = userService.getFavoriteStores(userId);
        return ResponseBuilder.ok(stores);
    }

    @GetMapping("/coupon")
    public CommonResponse getUserCoupons() {
        Long userId = UserHolder.getId();

        List<UserCouponResponse> coupons = userService.getUserCoupons(userId);
        return ResponseBuilder.ok(coupons);
    }

    @PostMapping("/coupon/claim")
    public CommonResponse claimCoupon(@Valid @RequestBody UserClaimCouponRequest request) {
        Long userId = UserHolder.getId();
        boolean success = userService.claimCoupon(userId, request.getType());
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("领取失败");
    }

    @GetMapping("/current")
    public CommonResponse getCurrentOrders() {
        Long userId = UserHolder.getId();
        UserCurrentOrderResponse response = userService.getCurrentOrders(userId);
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/search")
    public CommonResponse searchStoreAndProduct(@Valid @RequestBody UserSearchRequest request) {
        String keyword = request.getKeyWord();
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseBuilder.fail("关键词不能为空");
        }

        List<Map<String, Object>> raw = userService.searchStoreAndProductByKeyword(keyword.trim());

        List<UserSearchResponse> responses = raw.stream().map(item -> {
            UserSearchResponse resp = new UserSearchResponse();
            resp.setStoreId((Long) item.get("store_id"));
            resp.setStoreName((String) item.get("store_name"));
            resp.setProducts((Map<String, Long>) item.get("products"));
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("results", responses));
    }


    @PostMapping("/review")
    public CommonResponse submitReview(@Valid @RequestBody UserReviewRequest request) {
        Long userId = UserHolder.getId();
        UserReviewResponse response = new UserReviewResponse();
        userService.submitReview(userId, request);
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/order")
    public CommonResponse placeOrder(@Valid @RequestBody OrderCreateRequest request) {
        Long userId = UserHolder.getId();
        try {
            // 假设你有orderService，并且createOrder接口和之前OrderServiceImpl一致
            UserCreateOrderResponse response = orderService.createOrder(userId, request);
            return ResponseBuilder.ok(response);
        } catch (Exception e) {
            // 可以加日志 e.printStackTrace();
            return ResponseBuilder.fail("下单失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/cancel")
    public CommonResponse cancelAccount() {
        Long userId = UserHolder.getId();
        boolean success = userService.cancelAccount(userId);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("注销失败");
    }
}