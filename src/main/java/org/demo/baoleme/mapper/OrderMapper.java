package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.dto.response.user.UserCurrentOrderResponse;
import org.demo.baoleme.dto.response.user.UserOrderHistoryResponse;
import org.demo.baoleme.pojo.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 查询可抢订单（状态为等待，且 rider_id 为空）
     */
    @Select("SELECT * FROM `order` WHERE status = 0 AND rider_id IS NULL ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Order> selectAvailableOrders(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT * FROM `order` WHERE status = 0 AND rider_id IS NULL ORDER BY RAND() LIMIT 1")
    Order selectRandomOrderToSend();

    /**
     * 尝试抢单（加乐观锁，确保 rider_id 为空时才能更新）
     */
    @Update("UPDATE `order` SET rider_id = #{riderId}, status = 1 WHERE id = #{orderId} AND rider_id IS NULL AND status = 0")
    int grabOrder(@Param("orderId") Long orderId, @Param("riderId") Long riderId);

    /**
     * 骑手取消订单（只能取消 rider_id 是自己并且订单状态是 1）
     */
    @Update("UPDATE `order` SET rider_id = NULL, status = 0 WHERE id = #{orderId} AND rider_id = #{riderId} AND status = 1")
    int riderCancelOrder(@Param("orderId") Long orderId, @Param("riderId") Long riderId);

    /**
     * 骑手更新订单状态
     */
    @Update("UPDATE `order` SET status = #{status} WHERE id = #{orderId} AND rider_id = #{riderId}")
    int riderUpdateOrderStatus(@Param("orderId") Long orderId, @Param("riderId") Long riderId, @Param("status") Integer status);

    /**
     * 查询骑手历史订单（支持状态、时间范围、分页）
     */
    @Select("""
            SELECT * FROM `order`
            WHERE rider_id = #{riderId}
            AND (#{status} IS NULL OR status = #{status})
            AND (#{startTime} IS NULL OR created_at >= #{startTime})
            AND (#{endTime} IS NULL OR created_at <= #{endTime})
            ORDER BY created_at DESC
            LIMIT #{offset}, #{limit}
            """)
    List<Order> selectRiderOrders(@Param("riderId") Long riderId,
                                  @Param("status") Integer status,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    /**
     * 查询骑手收入统计
     */
    @Select("""
        SELECT
            COUNT(*) AS completed_orders,
            IFNULL(SUM(delivery_price), 0) AS total_earnings,
            IFNULL(SUM(CASE WHEN DATE_FORMAT(created_at, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m') THEN delivery_price ELSE 0 END), 0) AS current_month
        FROM `order`
        WHERE rider_id = #{riderId} AND status = 3
        """)
    Map<String, Object> selectRiderEarnings(@Param("riderId") Long riderId);

    /**
     * 骑手完成订单（状态改为3，并设置结束时间）
     */
    @Update("UPDATE `order` SET status = 3, ended_at = NOW() WHERE id = #{orderId} AND rider_id = #{riderId}")
    int completeOrder(@Param("orderId") Long orderId, @Param("riderId") Long riderId);


    @Select("""
    SELECT * FROM `order`
    WHERE (#{userId} IS NULL OR user_id = #{userId})
      AND (#{storeId} IS NULL OR store_id = #{storeId})
      AND (#{riderId} IS NULL OR rider_id = #{riderId})
      AND (#{status} IS NULL OR status = #{status})
      AND (#{createdAt} IS NULL OR created_at >= #{createdAt})
      AND (#{endedAt} IS NULL OR ended_at <= #{endedAt})
    ORDER BY id DESC
    LIMIT #{offset}, #{limit}
""")
    List<Order> selectOrdersPaged(@Param("userId") Long userId,
                                  @Param("storeId") Long storeId,
                                  @Param("riderId") Long riderId,
                                  @Param("status") Integer status,
                                  @Param("createdAt") LocalDateTime createdAt,
                                  @Param("endedAt") LocalDateTime endedAt,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    @Select("""
    SELECT order_item.product_id, p.name AS product_name, o.created_at
    FROM order_item
    JOIN product p ON order_item.product_id = p.id
    JOIN `order` o ON order_item.order_id = o.id
    WHERE o.user_id = #{userId}
    ORDER BY o.created_at DESC
""")
    List<UserOrderHistoryResponse> selectOrderHistoryByUserId(Long userId);


    @Select("""
    SELECT MAX(DATE_ADD(o.created_at, INTERVAL 30 MINUTE)) AS predict_time
    FROM `order` o
    WHERE o.user_id = #{userId} AND o.status IN (0, 1)
""")
    String selectPredictTimeByUserId(Long userId);

    @Select("SELECT COUNT(*) > 0 FROM `order` WHERE user_id = #{userId} AND id = #{orderId}")
    boolean existsUserOrder(Long userId, Long orderId);
    @Select("SELECT COUNT(*) > 0 FROM review WHERE user_id = #{userId} AND store_id = #{storeId}")
    boolean existsReview(Long userId, Long orderId);

    @Insert("""
    INSERT INTO review(user_id, store_id, product_id, rating, comment, image)
    VALUES (#{userId}, #{storeId}, #{productId}, #{rating}, #{comment}, #{image})
""")
    int insertReview(@Param("userId") Long userId,
                     @Param("storeId") Long storeId,
                     @Param("productId") Long productId,
                     @Param("rating") Integer rating,
                     @Param("comment") String comment,
                     @Param("image") String image);

    @Update("UPDATE `order` SET status = #{newStatus} WHERE id = #{orderId}")
    int updateByMerchant(
            @Param("orderId") Long orderId,
            @Param("newStatus") Integer newStatus
    );

    @Select("""
            SELECT * FROM `order`
            WHERE store_id = #{storeId}
            """)
    List<Order> selectByStoreId(@Param("storeId") Long storeId);

    @Select("""
            SELECT *
            FROM `order`
            WHERE store_id = #{storeId}
            AND (status IS NULL OR status = #{status})
            LIMIT #{offset}, #{pageSize}                                
            """)
    List<Order> selectByStoreIdUsingPage(
            @Param("storeId") Long storeId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize,
            @Param("status") Integer status
    );

    @Select("""
    SELECT o.id, o.created_at, o.ended_at, o.status,o.total_price,o.actual_price,
           s.name AS store_name, o.remark,o.user_location,o.store_location,o.store_id,o.rider_id,
           r.username AS rider_name, r.phone AS rider_phone
    FROM `order` o
    LEFT JOIN store s ON o.store_id = s.id
    LEFT JOIN rider r ON o.rider_id = r.id
    WHERE o.user_id = #{userId}
      AND (#{status} IS NULL OR o.status = #{status})
      AND (#{startTime} IS NULL OR o.created_at >= #{startTime})
      AND (#{endTime} IS NULL OR o.created_at <= #{endTime})
    ORDER BY o.created_at DESC
    LIMIT #{offset}, #{limit}
""")
    List<Map<String, Object>> selectUserOrders(
            @Param("userId") Long userId,
            @Param("status") Integer status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Select("""
    SELECT 
        o.id AS order_id, 
        o.created_at, 
        o.status,
        o.remark,
        o.user_location,
        s.location AS store_location,
        o.total_price,
        o.actual_price,
        o.delivery_price,
        s.name AS store_name,
        m.phone AS store_phone,   -- 从 merchant 表获取
        r.username AS rider_name,
        r.phone AS rider_phone
    FROM `order` o
    JOIN store s ON o.store_id = s.id
    JOIN merchant m ON s.merchant_id = m.id  -- 新增关联
    LEFT JOIN rider r ON o.rider_id = r.id
    WHERE o.user_id = #{userId} AND o.status IN (0, 1, 2)
    ORDER BY o.created_at DESC
    LIMIT #{offset}, #{limit}
""")
    List<Map<String, Object>> selectCurrentOrdersByUser(@Param("userId") Long userId,
                                                        @Param("offset") int offset,
                                                        @Param("limit") int limit);

    @Select("SELECT oi.quantity, p.name, p.description, p.price, p.image " +
            "FROM order_item oi " +
            "JOIN product p ON oi.product_id = p.id " +
            "WHERE oi.order_id = #{orderId}")
    List<Map<String, Object>> selectOrderItemsWithProductInfo(@Param("orderId") Long orderId);

    @Select("SELECT total_price, actual_price, delivery_price FROM `order` WHERE id = #{orderId}")
    Map<String, BigDecimal> getPriceInfoById(Long orderId);
}