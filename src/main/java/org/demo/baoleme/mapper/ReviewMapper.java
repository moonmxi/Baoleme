package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.demo.baoleme.pojo.Review;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    @Select("""
        SELECT id, user_id, store_id, product_id, rating, comment, created_at
        FROM review
        WHERE (#{userId} IS NULL OR user_id = #{userId})
          AND (#{storeId} IS NULL OR store_id = #{storeId})
          AND (#{productId} IS NULL OR product_id = #{productId})
          AND (#{startTime} IS NULL OR created_at >= #{startTime})
          AND (#{endTime} IS NULL OR created_at <= #{endTime})
          AND (#{startRating} IS NULL OR rating >= #{startRating})
          AND (#{endRating} IS NULL OR rating <= #{endRating})
        ORDER BY created_at DESC
        LIMIT #{offset}, #{limit}
    """)
    List<Review> selectReviewsByCondition(@Param("userId") Long userId,
                                    @Param("storeId") Long storeId,
                                    @Param("productId") Long productId,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit,
                                          @Param("startRating") BigDecimal startRating,
                                    @Param("endRating") BigDecimal endRating);

    /**
     * 查询指定店铺的高分评价（4-5星）
     * @param storeId 店铺ID
     * @return 高分评价列表
     */
    @Select("SELECT * FROM review WHERE store_id = #{storeId} AND rating BETWEEN 4 AND 5")
    List<Review> selectHighRatingReviews(Long storeId);

    /**
     * 查询指定店铺的低分评价（1-2星）
     * @param storeId 店铺ID
     * @return 低分评价列表
     */
    @Select("SELECT * FROM review WHERE store_id = #{storeId} AND rating BETWEEN 1 AND 2")
    List<Review> selectLowRatingReviews(Long storeId);

}