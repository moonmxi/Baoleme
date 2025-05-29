package org.demo.baoleme.controller;

import jakarta.validation.Valid;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.order.OrderCreateRequest;
import org.demo.baoleme.dto.request.order.UserOrderItemHistoryRequest;
import org.demo.baoleme.dto.request.user.*;
import org.demo.baoleme.dto.response.order.UserOrderItemHistoryResponse;
import org.demo.baoleme.dto.response.user.*;
import org.demo.baoleme.mapper.OrderMapper;
import org.demo.baoleme.pojo.User;
import org.demo.baoleme.service.UserService;
import org.demo.baoleme.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    @Autowired
    private OrderMapper orderMapper;

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


        boolean success = userService.updateInfo(user);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("登出失败");
    }

    @DeleteMapping("/delete")
    public CommonResponse delete() {
        boolean ok = userService.delete(UserHolder.getId());
        return ok ? ResponseBuilder.ok() : ResponseBuilder.fail("注销失败");
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
    public CommonResponse update(@Valid @RequestBody UserUpdateRequest request, @RequestHeader("Authorization") String tokenHeader) {
        Long id = UserHolder.getId();

        // 查询旧数据，判断是否修改了 username
        User before = userService.getInfo(id);
        if (before == null) {
            return ResponseBuilder.fail("用户不存在");
        }

        // 组装更新 user
        User user = new User();
        user.setId(id);
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setDescription(request.getDescription());
        user.setLocation(request.getLocation());
        user.setGender(request.getGender());

        boolean success = userService.updateInfo(user);
        if (!success) {
            return ResponseBuilder.fail("更新失败，请检查字段");
        }

        System.out.println(before.getUsername());
        System.out.println(request.getUsername());
        // 判断是否修改了 username
        boolean usernameChanged = request.getUsername() != null && !request.getUsername().equals(before.getUsername());
        if (!usernameChanged) {
            return ResponseBuilder.ok();  // 没改用户名，直接返回 OK
        }

        // ✅ 修改了 username，重新签发 token 并刷新 Redis
        String oldToken = tokenHeader.replace("Bearer ", "");
        String oldTokenKey = "user:token:" + oldToken;
        String oldLoginKey = "user:login:" + id;

        redisTemplate.delete(oldTokenKey);
        redisTemplate.delete(oldLoginKey);

        String newToken = JwtUtils.createToken(id, "user", request.getUsername());
        redisTemplate.opsForValue().set("user:token:" + newToken, id, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set("user:login:" + id, newToken, 1, TimeUnit.DAYS);

        String username = request.getUsername();
        String password = request.getPassword();
        String phone = request.getPhone();
        String avatar= request.getAvatar();
        String description = request.getDescription();
        String location = request.getLocation();
        String gender = request.getGender();

        // 返回新的 token
        UserLoginResponse response = new UserLoginResponse();
        response.setToken(newToken);
        response.setUsername(request.getUsername());
        response.setUserId(id);
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/history")
    public CommonResponse getOrderHistory(@RequestBody UserOrderHistoryRequest request) {
        Long userId = UserHolder.getId();
        String role = UserHolder.getRole();
        if (!"user".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅普通用户可操作");
        }

        List<Map<String, Object>> records = userService.getUserOrdersPaged(
                userId,
                request.getStatus(),
                request.getStartTime(),
                request.getEndTime(),
                request.getPage(),
                request.getPageSize()
        );

        List<UserOrderHistoryResponse> responses = records.stream().map(map -> {
            UserOrderHistoryResponse resp = new UserOrderHistoryResponse();
            resp.setOrderId((Long) map.get("id"));
            resp.setCreatedAt(map.get("created_at") != null ?
                    ((Timestamp) map.get("created_at")).toLocalDateTime() : null);
            resp.setEndedAt(map.get("ended_at") != null ?
                    ((Timestamp) map.get("ended_at")).toLocalDateTime() : null);
            resp.setStatus((Integer) map.get("status"));
            resp.setStoreName((String) map.get("store_name"));
            resp.setRemark((String) map.get("remark"));
            resp.setRiderName((String) map.get("rider_name"));
            resp.setRiderPhone((String) map.get("rider_phone"));
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("orders", responses));
    }

    @PostMapping("/favorite")
    public CommonResponse favoriteStore(@Valid @RequestBody UserFavoriteRequest request) {
        Long userId = UserHolder.getId();
        boolean success = userService.favoriteStore(userId, request.getStoreId());
        System.out.println(userId+"  "+request.getStoreId());
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
    public CommonResponse getCurrentOrders(@Valid @RequestBody UserCurrentOrderRequest request) {
        Long userId = UserHolder.getId();
        List<Map<String, Object>> result = userService.getCurrentOrders(userId, request.getPage(), request.getPage_size());

        List<UserCurrentOrderResponse> response = result.stream().map(map -> {
            UserCurrentOrderResponse r = new UserCurrentOrderResponse();
            r.setOrderId(((Number) map.get("order_id")).longValue());

            Object ts = map.get("created_at");
            if (ts instanceof Timestamp) {
                r.setCreatedAt(((Timestamp) ts).toLocalDateTime());
            } else if (ts instanceof LocalDateTime) {
                r.setCreatedAt((LocalDateTime) ts);
            } else {
                r.setCreatedAt(null); // 或者抛异常
            }

            r.setStatus((Integer) map.get("status"));
            r.setStoreName((String) map.get("store_name"));
            r.setRiderName((String) map.get("rider_name"));
            r.setRiderPhone((String) map.get("rider_phone"));
            r.setRemark((String) map.get("remark"));
            return r;
        }).toList();

        return ResponseBuilder.ok(Map.of("orders", response));
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
        UserReviewResponse response = userService.submitReview(userId, request);
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/order")
    public CommonResponse UserCreateOrder(@Valid @RequestBody OrderCreateRequest request) {
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

    @PostMapping("/history/item")
    public CommonResponse getOrderItemHistory(@Valid @RequestBody UserOrderItemHistoryRequest request){
        Long orderId = request.getOrderId();

        UserOrderItemHistoryResponse response = new UserOrderItemHistoryResponse();
        //得到orderItem的查询值
        response.setOrderItemList(userService.getOrderItemHistory(orderId));
        //得到product的查询值
        response.setPriceInfo(orderMapper.getPriceInfoById(orderId));
        return ResponseBuilder.ok(response);
    }

    @DeleteMapping("/cancel")
    public CommonResponse cancelAccount() {
        Long userId = UserHolder.getId();
        boolean success = userService.cancelAccount(userId);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("注销失败");
    }
}
