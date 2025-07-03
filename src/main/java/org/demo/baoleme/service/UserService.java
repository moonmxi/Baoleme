package org.demo.baoleme.service;

import org.demo.baoleme.dto.response.user.*;
import org.demo.baoleme.pojo.Rider;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.pojo.User;
import org.demo.baoleme.dto.request.user.UserReviewRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户业务接口
 * 说明：此接口不涉及任何认证上下文，完全以 userId 和 user 对象为参数处理业务。
 */
public interface UserService {

    /**
     * 用户注册（不传入 ID，由框架生成）
     * @param user 新注册信息
     * @return User 成功注册的 User 对象，失败返回 null
     */
    User register(User user);

    /**
     * 用户登录验证
     * @param phone 手机号
     * @param password 密码明文
     * @return 验证成功返回 User，失败返回 null
     */
    User login(String phone, String password);

    boolean delete(Long riderId);
    /**
     * 获取用户信息
     * @param userId 用户主键 ID
     * @return User 或 null
     */
    User getInfo(Long userId);

    /**
     * 更新用户资料（需包含 ID）
     * @param user 要更新的 User 对象
     * @return true 表示成功，false 表示失败
     */
    boolean updateInfo(User user);

    /**
     * 用户注销（逻辑删除）
     * @param userId 主键 ID
     * @return true 表示成功，false 表示失败
     */
    boolean cancelAccount(Long userId);

    /**
     * 获取用户历史订单
     * @param userId 用户ID
     * @return 历史订单列表
     */
    List<UserOrderHistoryResponse> getOrderHistory(Long userId);

    /**
     * 收藏店铺
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 是否成功
     */
    boolean favoriteStore(Long userId, Long storeId);

    /**
     * 获取用户收藏的店铺
     * @param userId 用户ID
     * @return 收藏店铺列表
     */
    List<UserFavoriteResponse> getFavoriteStores(Long userId, String type, BigDecimal distance,BigDecimal wishPrice, BigDecimal startRating,BigDecimal endRating,Integer page,Integer pageSize);

    boolean deleteFavorite(Long userId, Long storeId);

    List<UserFavoriteResponse> getStores(Long userId, String type, BigDecimal distance,BigDecimal wishPrice, BigDecimal startRating,BigDecimal endRating,Integer page,Integer pageSize);
    /**
     * 获取用户优惠券
     * @param userId 用户ID
     * @return 优惠券列表
     */
    List<UserCouponResponse> getUserCoupons(Long userId,Long storeId);


    boolean claimCoupon(Long userId, Long id);

    /**
     * 获取用户当前订单
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 当前订单列表
     */
    List<Map<String, Object>> getCurrentOrders(Long userId, int page, int pageSize);

    /**
     * 全局搜索
     * @param keyword 关键词
     * @return 搜索结果
     */
    List<UserSearchResponse> searchStores(String keyword, BigDecimal distance,BigDecimal wishPrice, BigDecimal startRating,BigDecimal endRating,Integer page,Integer pageSize);


    /**
     * 获取商品列表
     * @param shopId 店铺ID(可选)
     * @param category 商品分类(可选)
     * @return 商品列表
     */
    List<UserGetProductResponse> getProducts(Long shopId, String category);

    /**
     * 提交评价
     * @param userId 用户ID
     * @param request 评价请求
     * @return 是否成功
     */
    UserReviewResponse submitReview(Long userId, UserReviewRequest request);

    List<Map<String, Object>> getUserOrdersPaged(
            Long userId, Integer status, LocalDateTime startTime, LocalDateTime endTime,
            int page, int pageSize
    );

    List<Map<String,Object>> getOrderItemHistory(Long orderId);

    String getMerchantPhoneByStoreId(Long storeId);

    boolean updateAvatar(Long userId, String avatarPath);

    boolean updateViewHistory(Long userId, Long storeId, LocalDateTime viewTime);

    List<Store> getViewHistory(Long userId, Integer page, Integer pageSize);
}