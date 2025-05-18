package org.demo.baoleme.controller;

import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Delete;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.admin.*;
import org.demo.baoleme.dto.response.admin.*;
import org.demo.baoleme.pojo.*;
import org.demo.baoleme.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        Admin admin = adminService.login(request.getAdminId(), request.getPassword());
        if (admin == null) {
            return ResponseBuilder.fail("账号或密码错误");
        }

        // 构建 Redis 中用于标识登录状态的 key
        String redisLoginKey = "admin:login:" + admin.getId();

        // 如果已经存在登录状态，则拒绝登录
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisLoginKey))) {
            return ResponseBuilder.fail("该账户已登录，请先登出后再登录");
        }

        // 生成 token
        String token = JwtUtils.createToken(admin.getId(), "admin", null);

        // 将 token 存入 Redis（用于拦截器校验）
        String redisTokenKey = "admin:token:" + token;
        redisTemplate.opsForValue().set(redisTokenKey, admin.getId(), 1, TimeUnit.DAYS);

        // 记录登录状态（防止重复登录）
        redisTemplate.opsForValue().set(redisLoginKey, token, 1, TimeUnit.DAYS);

        // 返回响应
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
        String redisTokenKey = "admin:token:" + token;

        // 删除 token -> id 映射
        Object userId = redisTemplate.opsForValue().get(redisTokenKey);
        if (userId != null) {
            String redisLoginKey = "admin:login:" + userId;
            redisTemplate.delete(redisLoginKey);
        }

        redisTemplate.delete(redisTokenKey);
        return ResponseBuilder.ok("登出成功");
    }

    @PostMapping("/userlist")
    public CommonResponse getUserList(@Valid @RequestBody AdminUserQueryRequest request) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        int page = request.getPage();
        int pageSize = request.getPage_size();
        List<User> userList = adminService.getAllUsersPaged(page, pageSize);

        List<AdminUserQueryResponse> responses = userList.stream().map(user -> {
            AdminUserQueryResponse resp = new AdminUserQueryResponse();
            resp.setId(user.getId());
            resp.setUsername(user.getUsername());
            resp.setPhone(user.getPhone());
            resp.setAvatar(user.getAvatar());
            resp.setCreated_at(user.getCreatedAt());
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("users", responses));
    }

    /**
     * 管理员分页查看骑手列表
     */
    @PostMapping("/riderlist")
    public CommonResponse getRiderList(@Valid @RequestBody AdminRiderQueryRequest request) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        int page = request.getPage();
        int pageSize = request.getPage_size();
        List<Rider> riderList = adminService.getAllRidersPaged(page, pageSize);

        List<AdminRiderQueryResponse> responses = riderList.stream().map(rider -> {
            AdminRiderQueryResponse resp = new AdminRiderQueryResponse();
            resp.setId(rider.getId());
            resp.setUsername(rider.getUsername());
            resp.setPhone(rider.getPhone());
            resp.setOrder_status(rider.getOrderStatus());
            resp.setDispatch_mode(rider.getDispatchMode());
            resp.setBalance(rider.getBalance());
            resp.setAvatar(rider.getAvatar());
            resp.setCreated_at(rider.getCreatedAt());
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("riders", responses));
    }

    @PostMapping("/merchantlist")
    public CommonResponse getMerchantList(@Valid @RequestBody AdminMerchantQueryRequest request) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        int page = request.getPage();
        int pageSize = request.getPage_size();
        List<Merchant> merchantList = adminService.getAllMerchantsPaged(page, pageSize);

        List<AdminMerchantQueryResponse> responses = merchantList.stream().map(merchant -> {
            AdminMerchantQueryResponse resp = new AdminMerchantQueryResponse();
            resp.setId(merchant.getId());
            resp.setUsername(merchant.getUsername());
            resp.setPhone(merchant.getPhone());
            resp.setAvatar(merchant.getAvatar());
            resp.setCreated_at(merchant.getCreatedAt());
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("merchants", responses));
    }

    @PostMapping("/storelist")
    public CommonResponse getStoreList(@Valid @RequestBody AdminStoreQueryRequest request) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        int page = request.getPage();
        int pageSize = request.getPage_size();
        List<Store> storeList = adminService.getAllStoresPaged(page, pageSize);

        List<AdminStoreQueryResponse> responses = storeList.stream().map(store -> {
            AdminStoreQueryResponse resp = new AdminStoreQueryResponse();
            resp.setId(store.getId());
            resp.setName(store.getName());
            resp.setDescription(store.getDescription());
            resp.setLocation(store.getLocation());
            resp.setRating(store.getRating());
            resp.setBalance(store.getBalance());
            resp.setStatus(store.getStatus());
            resp.setCreated_at(store.getCreatedAt());
            resp.setImage(store.getImage());
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("stores", responses));
    }

    @DeleteMapping("/delete")
    public CommonResponse delete(@RequestBody AdminDeleteRequest request) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        boolean hasAny = request.getUser_name() != null ||
                request.getRider_name() != null ||
                request.getMerchant_name() != null ||
                request.getStore_name() != null ||
                request.getProduct_name() != null;

        if (!hasAny) {
            return ResponseBuilder.fail("至少提供一个删除目标");
        }

        List<String> failed = new ArrayList<>();

        // 删除用户
        if (request.getUser_name() != null &&
                !adminService.deleteUserByUsername(request.getUser_name())) {
            failed.add("用户删除失败");
        }

        // 删除骑手
        if (request.getRider_name() != null &&
                !adminService.deleteRiderByUsername(request.getRider_name())) {
            failed.add("骑手删除失败");
        }

        // 删除商家
        if (request.getMerchant_name() != null &&
                !adminService.deleteMerchantByUsername(request.getMerchant_name())) {
            failed.add("商家删除失败");
        }

        // 删除商品（需要店铺名）
        if (request.getProduct_name() != null) {
            if (request.getStore_name() == null) {
                return ResponseBuilder.fail("删除商品必须同时提供所属店铺名");
            }
            if (!adminService.deleteProductByNameAndStore(request.getProduct_name(), request.getStore_name())) {
                failed.add("商品删除失败");
            }
        } else if (request.getStore_name() != null) {
            // 删除店铺（仅当未指定商品时才删除店铺）
            if (!adminService.deleteStoreByName(request.getStore_name())) {
                failed.add("店铺删除失败");
            }
        }

        if (!failed.isEmpty()) {
            return ResponseBuilder.fail("删除失败：" + String.join("；", failed));
        }

        return ResponseBuilder.ok("删除成功");
    }
}
