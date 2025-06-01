package org.demo.baoleme.service.impl;

import ch.qos.logback.classic.Logger;
import org.demo.baoleme.dto.request.user.UserReviewRequest;
import org.demo.baoleme.dto.response.user.*;
import org.demo.baoleme.mapper.*;
import org.demo.baoleme.pojo.*;
import org.demo.baoleme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RiderMapper riderMapper;

    private Logger log;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
    public List<UserFavoriteResponse> getFavoriteStores(Long userId) {
        return userMapper.selectFavoriteStoresWithDetails(userId);
    }

    // 优惠券功能转移到CouponMapper
    @Override
    public List<UserCouponResponse> getUserCoupons(Long userId) {
        return couponMapper.selectUserCouponsByUserId(userId);
    }

    @Override
    public boolean claimCoupon(Long userId, Integer type) {
        // 验证type是否为有效值(1或2)
        if (type != 1 && type != 2) {
            System.out.println("领取失败：无效的优惠券类型");
            return false;
        }

        // 从数据库中获取一张指定类型且未分配用户的优惠券
        Coupon availableCoupon = couponMapper.selectAvailableCouponByType(type);
        if (availableCoupon == null) {
            System.out.println("领取失败：该类型优惠券已领完或不存在");
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
    public List<Map<String, Object>> searchStoreAndProductByKeyword(String keyword) {
        List<Map<String, Object>> stores = storeMapper.searchStoresByKeyword(keyword);
        List<Map<String, Object>> products = storeMapper.searchProductsByKeyword(keyword);

        Map<Long, Map<String, Object>> resultMap = new LinkedHashMap<>();

        for (Map<String, Object> store : stores) {
            Object idObj = store.get("id");
            Object nameObj = store.get("name");

            Long storeId = (idObj instanceof Number) ? ((Number) idObj).longValue() : null;
            String storeName = (nameObj instanceof String) ? (String) nameObj : "";

            if (storeId == null) {
                // 可选：跳过无效数据
                continue;
            }

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("store_id", storeId);
            entry.put("store_name", storeName);
            entry.put("products", new LinkedHashMap<String, Long>());  // 初始化空的产品 map

            resultMap.put(storeId, entry);
        }

        // 添加商品
        for (Map<String, Object> product : products) {
            Long storeId = ((Number) product.get("store_id")).longValue();
            String storeName = (String) product.get("store_name");
            String productName = (String) product.get("product_name");
            Long productId = ((Number) product.get("product_id")).longValue();

            if (!resultMap.containsKey(storeId)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("store_id", storeId);
                entry.put("store_name", storeName);
                entry.put("products", new LinkedHashMap<String, Long>());
                resultMap.put(storeId, entry);
            }

            Map<String, Long> productMap = (Map<String, Long>) resultMap.get(storeId).get("products");
            productMap.put(productName, productId);
        }

        return new ArrayList<>(resultMap.values());
    }


    @Override
    public UserGetShopResponse getStoresByDescription(String type) {
        UserGetShopResponse response = new UserGetShopResponse();
        List<Store> stores = storeMapper.selectShopsByType(type);
        response.setData(stores);

        Integer total = storeMapper.countShopsByType(type);  // 这里要用 count 方法
        response.setTotal(total);
        return response;
    }


    @Override
    public UserGetProductResponse getProducts(Long shopId, String category) {
        UserGetProductResponse response = new UserGetProductResponse();
        response.setShopId(shopId);
        response.setCategory(category);
        response.setData(storeMapper.selectProducts(shopId, category));
        return response;
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
    public List<Map<String, Object>> getUserOrdersPaged(Long userId, Integer status, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.selectUserOrders(userId, status, startTime, endTime, offset, pageSize);
    }

    @Override
    public List<Map<String,Object>> getOrderItemHistory(Long orderId) {
        List<Map<String,Object>> items = orderMapper.selectOrderItemsWithProductInfo(orderId);

        return items;
    }


}
