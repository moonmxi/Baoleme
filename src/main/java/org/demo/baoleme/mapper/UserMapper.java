package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.dto.response.*;
import org.demo.baoleme.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return User 对象或 null
     */
    @Select("SELECT id, username, password, phone, gender, avatar, role, status " +
            "FROM user WHERE username = #{username} LIMIT 1")
    User selectByUsername(String username);

    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return User 对象或 null
     */
    @Select("SELECT id, username, password, phone, gender, avatar, role, status " +
            "FROM user WHERE phone = #{phone} LIMIT 1")
    User selectByPhone(String phone);

    /**
     * 查询用户历史订单
     * @param userId 用户ID
     * @return 历史订单列表
     */
    @Select("SELECT o.product_id, p.name as product_name, o.create_time " +
            "FROM orders o JOIN product p ON o.product_id = p.id " +
            "WHERE o.user_id = #{userId} AND o.status = 'completed' " +
            "ORDER BY o.create_time DESC")
    List<UserOrderHistoryResponse> selectOrderHistoryByUserId(Long userId);

    /**
     * 检查用户是否已收藏店铺
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 是否已收藏
     */
    @Select("SELECT COUNT(*) > 0 FROM user_favorite_store " +
            "WHERE user_id = #{userId} AND store_id = #{storeId}")
    boolean existsFavorite(Long userId, Long storeId);

    /**
     * 添加店铺收藏
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 影响行数
     */
    @Insert("INSERT INTO user_favorite_store(user_id, store_id) VALUES(#{userId}, #{storeId})")
    int insertFavorite(Long userId, Long storeId);

    /**
     * 获取用户收藏的店铺
     * @param userId 用户ID
     * @return 收藏店铺列表
     */
    @Select("SELECT s.id as store_id, s.name as store_name " +
            "FROM store s JOIN user_favorite_store ufs ON s.id = ufs.store_id " +
            "WHERE ufs.user_id = #{userId}")
    List<UserFavoriteResponse> selectFavoriteStoresByUserId(Long userId);

    /**
     * 获取用户优惠券
     * @param userId 用户ID
     * @return 优惠券列表
     */
    @Select("SELECT c.id as coupon_id, c.code, c.discount, c.expiration_date " +
            "FROM coupon c JOIN user_coupon uc ON c.id = uc.coupon_id " +
            "WHERE uc.user_id = #{userId} AND c.expiration_date > NOW()")
    List<UserCouponResponse> selectUserCouponsByUserId(Long userId);

    /**
     * 检查用户是否已领取优惠券
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 是否已领取
     */
    @Select("SELECT COUNT(*) > 0 FROM user_coupon " +
            "WHERE user_id = #{userId} AND coupon_id = #{couponId}")
    boolean existsUserCoupon(Long userId, Long couponId);

    /**
     * 用户领取优惠券
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 影响行数
     */
    @Insert("INSERT INTO user_coupon(user_id, coupon_id) VALUES(#{userId}, #{couponId})")
    int insertUserCoupon(Long userId, Long couponId);

    /**
     * 获取用户当前订单
     * @param userId 用户ID
     * @return 当前订单列表
     */
    @Select("SELECT o.product_id, p.name as product_name, o.create_time " +
            "FROM orders o JOIN product p ON o.product_id = p.id " +
            "WHERE o.user_id = #{userId} AND o.status IN ('pending', 'processing') " +
            "ORDER BY o.create_time DESC")
    List<UserCurrentOrderResponse.OrderItem> selectCurrentOrdersByUserId(Long userId);

    /**
     * 获取预计送达时间
     * @param userId 用户ID
     * @return 预计送达时间
     */
    @Select("SELECT MAX(DATE_ADD(create_time, INTERVAL 30 MINUTE)) as predict_time " +
            "FROM orders WHERE user_id = #{userId} AND status IN ('pending', 'processing')")
    String selectPredictTimeByUserId(Long userId);

    /**
     * 搜索商品
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 商品列表
     */
    @Select("SELECT p.id as product_id, p.name as product_name, p.price, s.name as shop_name " +
            "FROM product p JOIN store s ON p.store_id = s.id " +
            "WHERE p.name LIKE CONCAT('%', #{keyword}, '%') " +
            "LIMIT #{size} OFFSET #{offset}")
    List<UserSearchResponse.Product> searchProducts(String keyword,
                                                @Param("offset") int offset,
                                                @Param("size") int size);

    /**
     * 搜索店铺
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 店铺列表
     */
    @Select("SELECT id as shop_id, name as shop_name, rating " +
            "FROM store WHERE name LIKE CONCAT('%', #{keyword}, '%') " +
            "LIMIT #{size} OFFSET #{offset}")
    List<UserSearchResponse.Shop> searchShops(String keyword,
                                          @Param("offset") int offset,
                                          @Param("size") int size);

    /**
     * 统计搜索结果总数
     * @param keyword 关键词
     * @return 总数
     */
    @Select("SELECT (SELECT COUNT(*) FROM product WHERE name LIKE CONCAT('%', #{keyword}, '%')) + " +
            "(SELECT COUNT(*) FROM store WHERE name LIKE CONCAT('%', #{keyword}, '%')) as total")
    int countSearchResults(String keyword);

    /**
     * 按类型查询店铺
     * @param type 店铺类型
     * @param page 页码
     * @param size 每页大小
     * @return 店铺列表
     */
    @Select("SELECT id as shop_id, name as shop_name, type, rating, delivery_time, image " +
            "FROM store WHERE type = #{type} OR #{type} IS NULL " +
            "LIMIT #{size} OFFSET #{offset}")
    List<UserGetShopResponse.Shop> selectShopsByType(@Param("type") String type,
                                                  @Param("offset") int offset,
                                                  @Param("size") int size);

    /**
     * 统计店铺数量
     * @param type 店铺类型
     * @return 总数
     */
    @Select("SELECT COUNT(*) FROM store WHERE type = #{type} OR #{type} IS NULL")
    int countShopsByType(String type);

    /**
     * 查询商品
     * @param shopId 店铺ID
     * @param category 商品分类
     * @return 商品列表
     */
    @Select("SELECT id as product_id, name as product_name, price, sales, image " +
            "FROM product " +
            "WHERE (store_id = #{shopId} OR #{shopId} IS NULL) " +
            "AND (category = #{category} OR #{category} IS NULL)")
    List<UserGetProductResponse.Product> selectProducts(@Param("shopId") Long shopId,
                                                     @Param("category") String category);

    /**
     * 检查订单是否属于用户
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 是否属于
     */
    @Select("SELECT COUNT(*) > 0 FROM orders " +
            "WHERE user_id = #{userId} AND id = #{orderId}")
    boolean existsUserOrder(Long userId, Long orderId);

    /**
     * 检查是否已评价
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 是否已评价
     */
    @Select("SELECT COUNT(*) > 0 FROM review " +
            "WHERE user_id = #{userId} AND order_id = #{orderId}")
    boolean existsReview(Long userId, Long orderId);

    /**
     * 添加评价
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param rating 评分
     * @param comment 评论
     * @return 影响行数
     */
    @Insert("INSERT INTO review(user_id, order_id, rating, comment) " +
            "VALUES(#{userId}, #{orderId}, #{rating}, #{comment})")
    int insertReview(Long userId, Long orderId, Integer rating, String comment);

    /**
     * 检查店铺是否存在
     * @param storeId 店铺ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM store WHERE id = #{storeId}")
    boolean existsShop(Long storeId);

    /**
     * 检查商品是否存在
     * @param productId 商品ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM product WHERE id = #{productId}")
    boolean existsProduct(Long productId);

    /**
     * 检查优惠券是否可用
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 是否可用
     */
    @Select("SELECT c.id IS NOT NULL AND c.expiration_date > NOW() AND " +
            "(uc.id IS NULL OR uc.user_id = #{userId}) " +
            "FROM coupon c LEFT JOIN user_coupon uc ON c.id = uc.coupon_id AND uc.user_id = #{userId} " +
            "WHERE c.id = #{couponId}")
    boolean isCouponValid(Long userId, Long couponId);
}