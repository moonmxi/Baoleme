package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.dto.response.user.UserCurrentOrderResponse;
import org.demo.baoleme.dto.response.user.UserOrderHistoryResponse;
import org.demo.baoleme.pojo.Order;

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
            IFNULL(SUM(total_price), 0) AS total_earnings,
            IFNULL(SUM(CASE WHEN DATE_FORMAT(created_at, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m') THEN total_price ELSE 0 END), 0) AS current_month
        FROM `order`
        WHERE rider_id = #{riderId} AND status = 3
        """)
    Map<String, Object> selectRiderEarnings(@Param("riderId") Long riderId);

    /**
     * 骑手完成订单（状态改为3，并设置结束时间）
     */
    @Update("UPDATE `order` SET status = 3, ended_at = NOW() WHERE id = #{orderId} AND rider_id = #{riderId}")
    int completeOrder(@Param("orderId") Long orderId, @Param("riderId") Long riderId);

    /**
     * 查询单个订单（用于验证 rider 是否拥有该订单等）
     */
    @Select("SELECT * FROM `order` WHERE id = #{orderId}")
    Order selectById(@Param("orderId") Long orderId);

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
    SELECT oi.product_id, p.name AS product_name, o.created_at AS create_time
    FROM order_item oi
    JOIN product p ON oi.product_id = p.id
    JOIN `order` o ON oi.order_id = o.id
    WHERE o.user_id = #{userId} AND o.status = 3 -- 假设 3 是 completed
    ORDER BY o.created_at DESC
""")
    List<UserOrderHistoryResponse> selectOrderHistoryByUserId(Long userId);

    @Select("""
    SELECT oi.product_id, p.name AS product_name, o.created_at AS create_time
    FROM order_item oi
    JOIN product p ON oi.product_id = p.id
    JOIN `order` o ON oi.order_id = o.id
    WHERE o.user_id = #{userId} AND o.status IN (0, 1)
    ORDER BY o.created_at DESC
""")
    List<UserCurrentOrderResponse.OrderItem> selectCurrentOrdersByUserId(Long userId);

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

    // TODO: 使用OrderMapper.xml，实现可变参数
    @Select("""
            SELECT *
            FROM `order`
            WHERE store_id = #{storeId}
            LIMIT #{offset}, #{pageSize}                                
            """)
    List<Order> selectByStoreIdUsingPage(
            @Param("storeId") Long storeId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );
}