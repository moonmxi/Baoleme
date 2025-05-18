package org.demo.baoleme.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.demo.baoleme.pojo.Review;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReviewMapper {

    @Select("""
        SELECT id, user_id, store_id, product_id, rating, comment, created_at
        FROM review
        WHERE (#{userId} IS NULL OR user_id = #{userId})
          AND (#{storeId} IS NULL OR store_id = #{storeId})
          AND (#{productId} IS NULL OR product_id = #{productId})
          AND (#{startTime} IS NULL OR created_at >= #{startTime})
          AND (#{endTime} IS NULL OR created_at <= #{endTime})
        ORDER BY created_at DESC
        LIMIT #{offset}, #{limit}
    """)
    List<Review> selectReviewsByCondition(@Param("userId") Long userId,
                                    @Param("storeId") Long storeId,
                                    @Param("productId") Long productId,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);

}