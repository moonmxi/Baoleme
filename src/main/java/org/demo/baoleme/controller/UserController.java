package org.demo.baoleme.controller;

import jakarta.validation.Valid;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.coupon.AvailableCouponRequest;
import org.demo.baoleme.dto.request.order.OrderCreateRequest;
import org.demo.baoleme.dto.request.order.UserOrderItemHistoryRequest;
import org.demo.baoleme.dto.request.user.*;
import org.demo.baoleme.dto.response.order.UserOrderItemHistoryResponse;
import org.demo.baoleme.dto.response.user.*;
import org.demo.baoleme.mapper.OrderMapper;
import org.demo.baoleme.pojo.Order;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.pojo.User;
import org.demo.baoleme.service.UserService;
import org.demo.baoleme.service.OrderService;
import org.demo.baoleme.service.SalesStatsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
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
    private SalesStatsService saleStatsService;
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
                    ((LocalDateTime) map.get("created_at")) : null);
            resp.setEndedAt(map.get("ended_at") != null ?
                    ((LocalDateTime) map.get("ended_at")) : null);
            resp.setStatus((Integer) map.get("status"));
            resp.setStoreName((String) map.get("store_name"));
            resp.setRemark((String) map.get("remark"));
            resp.setRiderName((String) map.get("rider_name"));
            resp.setRiderPhone((String) map.get("rider_phone"));
            resp.setTotalPrice((BigDecimal) map.get("total_price"));
            resp.setActualPrice((BigDecimal) map.get("actual_price"));
            resp.setDeliveryPrice((BigDecimal) map.get("delivery_price"));
            resp.setUserLocation((String) map.get("user_location"));
            resp.setStoreLocation((String) map.get("store_location"));
            resp.setStoreId((Long) map.get("store_id"));
            resp.setRiderId((Long) map.get("rider_id"));


            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("orders", responses));
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
    @PostMapping("/favorite")
    public CommonResponse favoriteStore(@Valid @RequestBody UserFavoriteRequest request) {
        Long userId = UserHolder.getId();
        boolean success = userService.favoriteStore(userId, request.getStoreId());
        System.out.println(userId+"  "+request.getStoreId());
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("收藏失败");
    }

    @PostMapping("/favorite/watch")
    public CommonResponse getFavoriteStores(@Valid@RequestBody UserGetFavoriteStoresRequest request) {
        Long userId = UserHolder.getId();
        String type = request.getType();
        BigDecimal distance = request.getDistance();
        BigDecimal wishPrice = request.getWishPrice();
        BigDecimal startRating = request.getStartRating();
        BigDecimal endRating = request.getEndRating();
        Integer page = request.getPage();
        Integer pageSize = request.getPageSize();
        List<UserFavoriteResponse> stores = userService.getFavoriteStores(userId,type, distance,wishPrice,startRating,endRating,page,pageSize);

        return ResponseBuilder.ok(stores);
    }
    @PostMapping("/deleteFavorite")
    public CommonResponse deleteFavorite(@Valid @RequestBody UserDeleteFavoriteRequest request) {
        Long userId = UserHolder.getId();
        boolean success = userService.deleteFavorite(userId, request.getStoreId());
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("删除失败");
    }
    @PostMapping("/coupon")
    public CommonResponse getUserCoupons(@Valid @RequestBody UserViewCouponRequest request) {
        Long userId = UserHolder.getId();
        Long storeId = request.getStoreId();
        List<UserCouponResponse> coupons = userService.getUserCoupons(userId,storeId);
        return ResponseBuilder.ok(coupons);
    }
    @PostMapping("/coupon/view")
    public CommonResponse availableCoupons(@Valid @RequestBody AvailableCouponRequest request){
        Long storeId = request.getStoreId();
        List<UserCouponResponse> coupons = userService.getUserCoupons(0L,storeId);

        return ResponseBuilder.ok(coupons);
    }

    @PostMapping("/coupon/claim")
    public CommonResponse claimCoupon(@Valid @RequestBody UserClaimCouponRequest request) {
        Long userId = UserHolder.getId();
        boolean success = userService.claimCoupon(userId, request.getId());
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("领取失败");
    }



    @PostMapping("/current")
    public CommonResponse getCurrentOrders(@Valid @RequestBody UserCurrentOrderRequest request) {
        Long userId = UserHolder.getId();
        List<Map<String, Object>> result = userService.getCurrentOrders(userId, request.getPage(), request.getPageSize());

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
            r.setStorePhone((String) map.get("store_phone"));
            r.setRiderName((String) map.get("rider_name"));
            r.setRiderPhone((String) map.get("rider_phone"));
            r.setRemark((String) map.get("remark"));
            r.setStoreLocation((String) map.get("store_location"));
            r.setUserLocation((String) map.get("user_location"));

            r.setTotalPrice((BigDecimal) map.get("total_price"));
            r.setActualPrice((BigDecimal) map.get("actual_price"));
            r.setDeliveryPrice((BigDecimal) map.get("delivery_price"));

            return r;
        }).toList();

        return ResponseBuilder.ok(Map.of("orders", response));
    }

    @PostMapping("/search")
    public CommonResponse searchStoreAndProduct(@Valid @RequestBody UserSearchRequest request) {
        String keyword = request.getKeyword();
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseBuilder.fail("关键词不能为空");
        }
        BigDecimal  distance = request.getDistance();
        BigDecimal wishPrice = request.getWishPrice();
        BigDecimal startRating = request.getStartRating();
        BigDecimal endRating = request.getEndRating();
        Integer page = request.getPage();
        Integer pageSize = request.getPageSize();

        List<UserSearchResponse> stores = userService.searchStores(keyword.trim(),distance,wishPrice,startRating,endRating,page,pageSize);


        return ResponseBuilder.ok(Map.of("reults", stores));
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



    @DeleteMapping("/cancel")
    public CommonResponse cancelAccount() {
        Long userId = UserHolder.getId();
        boolean success = userService.cancelAccount(userId);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("注销失败");
    }

    @PostMapping("/searchOrder")
     public CommonResponse searchOrder(@Valid @RequestBody UserSearchOrderRequest request) {
        try {
            Order order = orderService.getOrderById(request.getOrderId());
            UserSearchOrderResponse response = new UserSearchOrderResponse();
            response.setId(order.getId());
            response.setUserId(order.getUserId());
            response.setStoreId(order.getStoreId());
            response.setRiderId(order.getRiderId());
            response.setStatus(order.getStatus());
            response.setUserLocation(order.getUserLocation());
            response.setStoreLocation(order.getStoreLocation());
            response.setTotalPrice(order.getTotalPrice());
            response.setActualPrice(order.getActualPrice());
            response.setDeliveryPrice(order.getDeliveryPrice());
            response.setRemark(order.getRemark());
            response.setCreatedAt(order.getCreatedAt());
            response.setDeadline(order.getDeadline());
            response.setEndedAt(order.getEndedAt());
            response.setStorePhone(userService.getMerchantPhoneByStoreId(order.getStoreId()));

            return ResponseBuilder.ok(response);
        } catch (Exception e){
            return ResponseBuilder.fail("订单不存在");
        }


    }
    @PostMapping("/searchOrderItem")
    public CommonResponse searchOrderItem(@Valid @RequestBody UserSearchOrderRequest request) {
        try {
            Order order = orderService.getOrderById(request.getOrderId());
            List<UserSearchOrderItemResponse> response = orderService.getOrderItemById(order.getId());

            return ResponseBuilder.ok(response);
        } catch (Exception e){
            return ResponseBuilder.fail("订单明细不存在");
        }

    }

    @PostMapping("/updateViewHistory")
    public CommonResponse updateViewHistory(@Valid @RequestBody UserUpdateViewHistoryRequest request) {
        Long userId = UserHolder.getId();
        Long storeId = request.getStoreId();
        LocalDateTime viewTime = request.getViewTime();
        boolean success = userService.updateViewHistory(userId, storeId,  viewTime);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("添加失败");
    }
    @PostMapping("/viewHistory")
    public CommonResponse getViewHistory(@Valid @RequestBody UserGetViewHistoryRequest request) {
        Long userId = UserHolder.getId();
        Integer page = request.getPage();
        Integer pageSize = request.getPageSize();
        List<Store> response = userService.getViewHistory(userId, page, pageSize);

        return ResponseBuilder.ok(Map.of("stores", response));
    }
}
