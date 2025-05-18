package org.demo.baoleme.service;

import org.demo.baoleme.dto.response.*;
import org.demo.baoleme.pojo.User;
import org.demo.baoleme.dto.request.UserReviewRequest;
import org.demo.baoleme.dto.request.UserCreateOrderRequest;

import java.util.List;

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
    List<UserFavoriteResponse> getFavoriteStores(Long userId);

    /**
     * 获取用户优惠券
     * @param userId 用户ID
     * @return 优惠券列表
     */
    List<UserCouponResponse> getUserCoupons(Long userId);

    /**
     * 领取优惠券
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 是否成功
     */
    boolean claimCoupon(Long userId, Long couponId);

    /**
     * 获取当前订单
     * @param userId 用户ID
     * @return 当前订单信息
     */
    UserCurrentOrderResponse getCurrentOrders(Long userId);

    /**
     * 全局搜索
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    UserSearchResponse search(String keyword, int page, int size);

    /**
     * 获取商家列表
     * @param type 商家类型(可选)
     * @param page 页码
     * @param size 每页大小
     * @return 商家列表
     */
    UserGetShopResponse getShops(String type, int page, int size);

    /**
     * 获取商品列表
     * @param shopId 店铺ID(可选)
     * @param category 商品分类(可选)
     * @return 商品列表
     */
    UserGetProductResponse getProducts(Long shopId, String category);

    /**
     * 提交评价
     * @param userId 用户ID
     * @param request 评价请求
     * @return 是否成功
     */
    boolean submitReview(Long userId, UserReviewRequest request);

    /**
     * 下单
     * @param userId 用户ID
     * @param request 订单请求
     * @return 订单响应
     */
    UserCreateOrderResponse placeOrder(Long userId, UserCreateOrderRequest request);

    UserCreateOrderResponse placeOrder(Long userId, UserCreateOrderResponse request);
}