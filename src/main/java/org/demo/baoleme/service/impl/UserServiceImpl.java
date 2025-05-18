package org.demo.baoleme.service.impl;

import org.demo.baoleme.dto.request.UserCreateOrderRequest;
import org.demo.baoleme.dto.request.UserReviewRequest;
import org.demo.baoleme.dto.response.*;
import org.demo.baoleme.mapper.UserMapper;
import org.demo.baoleme.pojo.User;
import org.demo.baoleme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User register(User user) {
        // 基本字段不能为空
        if (!StringUtils.hasText(user.getUsername())) {
            System.out.println("注册失败：用户名为空");
            return null;
        }
        if (!StringUtils.hasText(user.getPassword())) {
            System.out.println("注册失败：密码为空");
            return null;
        }
        if (!StringUtils.hasText(user.getPhone())) {
            System.out.println("注册失败：手机号为空");
            return null;
        }

        // 校验用户名是否已存在
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            System.out.println("注册失败：用户名已存在");
            return null;
        }

        // 校验手机号是否已存在
        if (userMapper.selectByPhone(user.getPhone()) != null) {
            System.out.println("注册失败：手机号已注册");
            return null;
        }

        // 初始化字段
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        boolean inserted = userMapper.insert(user) > 0;
        return inserted ? user : null;
    }

    @Override
    public User login(String phone, String rawPassword) {
        User user = userMapper.selectByPhone(phone);
        if (user == null) {
            System.out.println("登录失败：手机号不存在");
            return null;
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            System.out.println("登录失败：密码错误");
            return null;
        }

        return user;
    }

    @Override
    public User getInfo(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public boolean updateInfo(User user) {
        if (user == null || user.getId() == null) return false;

        User existing = userMapper.selectById(user.getId());
        if (existing == null) return false;

        if (StringUtils.hasText(user.getUsername())) {
            // 检查新用户名是否已被其他用户使用
            User byUsername = userMapper.selectByUsername(user.getUsername());
            if (byUsername != null && !byUsername.getId().equals(user.getId())) {
                System.out.println("更新失败：用户名已被其他用户使用");
                return false;
            }
            existing.setUsername(user.getUsername());
        }
        if (StringUtils.hasText(user.getPassword())) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (StringUtils.hasText(user.getPhone())) {
            // 检查新手机号是否已被其他用户使用
            User byPhone = userMapper.selectByPhone(user.getPhone());
            if (byPhone != null && !byPhone.getId().equals(user.getId())) {
                System.out.println("更新失败：手机号已被其他用户使用");
                return false;
            }
            existing.setPhone(user.getPhone());
        }
        if (StringUtils.hasText(user.getGender())) {
            existing.setGender(user.getGender());
        }
        if (StringUtils.hasText(user.getAvatar())) {
            existing.setAvatar(user.getAvatar());
        }

        return userMapper.updateById(existing) > 0;
    }

    @Override
    public boolean cancelAccount(Long userId) {
        // 软删除，设置状态为0表示已注销
        User user = userMapper.selectById(userId);
        if (user == null) return false;

        return userMapper.updateById(user) > 0;
    }

    @Override
    public List<UserOrderHistoryResponse> getOrderHistory(Long userId) {
        return userMapper.selectOrderHistoryByUserId(userId);
    }

    @Override
    public boolean favoriteStore(Long userId, Long storeId) {
        // 检查是否已收藏
        if (userMapper.existsFavorite(userId, storeId)) {
            System.out.println("收藏失败：已收藏该店铺");
            return false;
        }
        return userMapper.insertFavorite(userId, storeId) > 0;
    }

    @Override
    public List<UserFavoriteResponse> getFavoriteStores(Long userId) {
        return userMapper.selectFavoriteStoresByUserId(userId);
    }

    @Override
    public List<UserCouponResponse> getUserCoupons(Long userId) {
        return userMapper.selectUserCouponsByUserId(userId);
    }

    @Override
    public boolean claimCoupon(Long userId, Long couponId) {
        // 检查是否已领取
        if (userMapper.existsUserCoupon(userId, couponId)) {
            System.out.println("领取失败：已领取该优惠券");
            return false;
        }
        return userMapper.insertUserCoupon(userId, couponId) > 0;
    }

    @Override
    public UserCurrentOrderResponse getCurrentOrders(Long userId) {
        UserCurrentOrderResponse response = new UserCurrentOrderResponse();
        response.setData(userMapper.selectCurrentOrdersByUserId(userId));
        response.setPredictTime(userMapper.selectPredictTimeByUserId(userId));
        return response;
    }

    @Override
    public UserSearchResponse search(String keyword, int page, int size) {
        UserSearchResponse response = new UserSearchResponse();
        response.setProducts(userMapper.searchProducts(keyword, page, size));
        response.setShops(userMapper.searchShops(keyword, page, size));
        response.setTotal(userMapper.countSearchResults(keyword));
        return response;
    }

    @Override
    public UserGetShopResponse getShops(String type, int page, int size) {
        UserGetShopResponse response = new UserGetShopResponse();
        response.setData(userMapper.selectShopsByType(type, page, size));
        response.setTotal(userMapper.countShopsByType(type));
        return response;
    }

    @Override
    public UserGetProductResponse getProducts(Long shopId, String category) {
        UserGetProductResponse response = new UserGetProductResponse();
        response.setData(userMapper.selectProducts(shopId, category));
        return response;
    }

    @Override
    public boolean submitReview(Long userId, UserReviewRequest request) {
        // 检查订单是否属于该用户
        if (!userMapper.existsUserOrder(userId, request.getOrderId())) {
            System.out.println("评价失败：订单不属于该用户");
            return false;
        }
        // 检查是否已评价
        if (userMapper.existsReview(userId, request.getOrderId())) {
            System.out.println("评价失败：已评价过该订单");
            return false;
        }
        return userMapper.insertReview(userId, request.getOrderId(),
                request.getRating(), request.getComment()) > 0;
    }

    @Override
    public UserCreateOrderResponse placeOrder(Long userId, UserCreateOrderRequest request) {
        return null;
    }

    @Override
    public UserCreateOrderResponse placeOrder(Long userId, UserCreateOrderResponse request) {
        // 1. 验证店铺和商品是否存在
        if (!userMapper.existsShop(request.getStoreId())) {
            System.out.println("下单失败：店铺不存在");
            return null;
        }

        for (UserCreateOrderResponse.OrderItem item : request.getItems()) {
            if (!userMapper.existsProduct(item.getProductId())) {
                System.out.println("下单失败：商品不存在");
                return null;
            }
        }

        // 2. 验证优惠券是否可用
        if (request.getCouponId() != null &&
                !userMapper.isCouponValid(userId, request.getCouponId())) {
            System.out.println("下单失败：优惠券不可用");
            return null;
        }

        // 3. 创建订单
        UserCreateOrderResponse response = new UserCreateOrderResponse();
        // 这里应该实现完整的订单创建逻辑
        // 包括计算总价、生成订单号、设置支付URL等

        return response;
    }
}