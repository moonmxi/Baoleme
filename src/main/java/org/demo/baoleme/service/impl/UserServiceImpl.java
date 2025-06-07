package org.demo.baoleme.service.impl;

import ch.qos.logback.classic.Logger;
import org.demo.baoleme.dto.request.user.UserReviewRequest;
import org.demo.baoleme.dto.response.user.*;
import org.demo.baoleme.mapper.*;
import org.demo.baoleme.pojo.*;
import org.demo.baoleme.service.SalesStatsService;
import org.demo.baoleme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SalesStatsService salesStatsService;

    @Autowired
    private RiderMapper riderMapper;

    private Logger log;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private SalesStatsServiceImpl salesStatsServiceImpl;

    // 用户核心功能保持不变
    @Override
    public User register(User user) {
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

        if (userMapper.selectByUsername(user.getUsername()) != null) {
            System.out.println("注册失败：用户名已存在");
            return null;
        }

        if (userMapper.selectByPhone(user.getPhone()) != null) {
            System.out.println("注册失败：手机号已注册");
            return null;
        }

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
    public boolean delete(Long userId) {
        return userMapper.deleteById(userId) > 0;
    }
    @Override
    public User getInfo(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public boolean updateInfo(User user) {
        if (user == null || user.getId() == null) return false;

        User existing = userMapper.selectById(user.getId());
        existing.setId(user.getId());
        if (existing == null) return false;

        if (StringUtils.hasText(user.getUsername())) {
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
            //System.out.println("111");
            User byPhone = userMapper.selectByPhone(user.getPhone());
            if (byPhone != null && !byPhone.getId().equals(user.getId())) {
                System.out.println("更新失败：手机号已被其他用户使用");
                return false;
            }
            existing.setPhone(user.getPhone());
            //System.out.println(existing.getPhone());
        }
        if (StringUtils.hasText(user.getDescription())) {
            existing.setDescription(user.getDescription());
        }
        if (StringUtils.hasText(user.getLocation())) {
            existing.setLocation(user.getLocation());
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
        return false;
    }

    // 收藏功能保持不变
    @Override
    public boolean favoriteStore(Long userId, Long storeId) {
        if (userMapper.existsFavorite(userId, storeId)) {
            System.out.println("收藏失败：已收藏该店铺");
            return false;
        }
        return userMapper.insertFavorite(userId, storeId) > 0;
    }

    @Override
    public List<UserFavoriteResponse> getFavoriteStores(Long userId,String type, BigDecimal distance,BigDecimal wishPrice, BigDecimal startRating, BigDecimal endRating, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        return userMapper.selectFavoriteStoresWithDetails(userId,type, distance,wishPrice,startRating,endRating,offset,pageSize);
    }
    @Override
    public boolean deleteFavorite(Long userId, Long storeId) {
        return userMapper.deleteFavorite(userId, storeId) > 0;
    }
    @Override
    public List<UserFavoriteResponse> getStores(Long userId,String type, BigDecimal distance,BigDecimal wishPrice, BigDecimal startRating, BigDecimal endRating, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        return userMapper.getStores(userId,type, distance,wishPrice,startRating,endRating,offset,pageSize);
    }
    // 优惠券功能转移到CouponMapper
    @Override
    public List<UserCouponResponse> getUserCoupons(Long userId,Long storeId) {
        return couponMapper.selectUserCouponsByUserId(userId,storeId);
    }

    @Override
    public boolean claimCoupon(Long userId, Long id) {

        // 从数据库中获取一张指定类型且未分配用户的优惠券
        Coupon availableCoupon = couponMapper.selectById(id);
        if (availableCoupon == null) {
            System.out.println("领取失败：该类型优惠券不存在");
            return false;
        }

        // 更新优惠券的用户ID
        Coupon updateCoupon = new Coupon();
        updateCoupon.setId(availableCoupon.getId());
        updateCoupon.setUserId(userId);

        int result = couponMapper.updateUserCoupon(availableCoupon.getId(), userId);
        if (result <= 0) {
            System.out.println("领取失败：更新优惠券用户ID失败");
            return false;
        }

        System.out.println("优惠券领取成功");
        return true;
    }

    // 订单功能转移到OrderMapper
    @Override
    public List<UserOrderHistoryResponse> getOrderHistory(Long userId) {
        return orderMapper.selectOrderHistoryByUserId(userId);
    }

    @Override
    public List<Map<String, Object>> getCurrentOrders(Long userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.selectCurrentOrdersByUser(userId, offset, pageSize);
    }

    @Override
    public List<UserSearchResponse> searchStores(String keyword, BigDecimal distance,BigDecimal wishPrice, BigDecimal startRating,BigDecimal endRating,Integer page,Integer pageSize) {
        int offset = (page - 1) * pageSize;
        return userMapper.searchStores(keyword,distance,wishPrice,startRating,endRating,offset,pageSize);

    }



    @Override
    public List<UserGetProductResponse> getProducts(Long shopId, String category) {

        return storeMapper.selectProducts(shopId, category);
    }

    @Override
    public UserReviewResponse submitReview(Long userId, UserReviewRequest request) {
        UserReviewResponse response = new UserReviewResponse();

        Long storeId = request.getStoreId();
        Long productId = request.getProductId();

        // 校验店铺是否存在
        String storeName = storeMapper.getNameById(storeId);
        if (storeName == null) {
            throw new IllegalArgumentException("无效的店铺ID");
        }
        response.setStoreName(storeName);

        // 如果 productId 不为 null，则查询商品名称
        String productName = null;
        if (productId != null) {
            productName = productMapper.getNameById(productId);
            if (productName == null) {
                throw new IllegalArgumentException("无效的商品ID");
            }
            response.setProductName(productName);
        }

        response.setComment(request.getComment());
        response.setRating(request.getRating());

        // 图片处理，建议 JSON 字符串存储
        String imagesStr = (request.getImages() == null || request.getImages().isEmpty())
                ? null
                : String.join(",", request.getImages());
        response.setImages(request.getImages());

        // 插入评论（可能为空的字段统一处理）
        orderMapper.insertReview(
                userId,
                storeId,
                productId,
                request.getRating(),
                request.getComment(),
                imagesStr
        );

        return response;
    }

    @Override
    public List<Map<String, Object>> getUserOrdersPaged(Long userId, Integer status, LocalDateTime startTime, LocalDateTime endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        System.out.println(" "+ startTime + " " + endTime + " " );
        return orderMapper.selectUserOrders(userId, status, startTime, endTime, offset, pageSize);
    }

    @Override
    public List<Map<String,Object>> getOrderItemHistory(Long orderId) {
        List<Map<String,Object>> items = orderMapper.selectOrderItemsWithProductInfo(orderId);

        return items;
    }

    @Override
    public String getMerchantPhoneByStoreId(Long storeId){
        return merchantMapper.selectPhoneByStoreId(storeId);
    }

    @Override
    public boolean updateAvatar(Long userId, String avatarPath) {
        if (userId == null || !StringUtils.hasText(avatarPath)) {
            return false;
        }
        int rows = userMapper.updateAvatarById(userId, avatarPath);
        return rows > 0;
    }
    @Override
    public boolean updateViewHistory(Long userId, Long storeId, LocalDateTime viewTime){
        return userMapper.addViewHistory(userId, storeId, viewTime) > 0;
    }
    @Override
    public List<Store> getViewHistory(Long userId, Integer page, Integer pageSize){
        int offset = (page - 1) * pageSize;
        return userMapper.selectViewHistory(userId, offset, pageSize);
    }
}
