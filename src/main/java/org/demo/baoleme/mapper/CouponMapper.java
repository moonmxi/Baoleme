package org.demo.baoleme.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.dto.response.user.*;
import org.demo.baoleme.pojo.Coupon;
import org.demo.baoleme.pojo.User;

import java.util.List;
public interface CouponMapper extends BaseMapper<Coupon> {
    // 检查用户是否已领取过同类型的优惠券
    @Select("SELECT COUNT(*) > 0 FROM coupon WHERE user_id = #{userId} AND type = " +
            "(SELECT type FROM coupon WHERE id = #{templateCouponId})")
    boolean hasUserClaimedCouponType(@Param("userId") Long userId,
                                     @Param("templateCouponId") Long templateCouponId);

    // 根据ID查询优惠券
    @Select("SELECT * FROM coupon WHERE id = #{couponId}")
    Coupon selectById(Long couponId);

    @Select("SELECT c.id as coupon_id, c.type, c.discount, c.expiration_date, " +
            "c.full_amount, c.reduce_amount " +
            "FROM coupon c WHERE (c.user_id IS NULL OR c.user_id = #{userId}) " +
            "AND (c.store_id IS NULL OR c.store_id = #{storeId})" +
            "AND (c.is_used = 0)")
    List<UserCouponResponse> selectUserCouponsByUserId(Long userId, Long storeId);

//    @Select("SELECT COUNT(*) > 0 FROM coupon " +
//            "WHERE id = #{couponId} AND user_id = #{userId}")
//    boolean existsUserCoupon(@Param("userId") Long userId,
//                             @Param("couponId") Long couponId);

    @Insert("INSERT INTO coupon(user_id, type, discount, expiration_date, type) " +
            "VALUES(#{userId}, #{code}, #{description}, #{discount}, #{expirationDate}, #{type})")
    int createUserCoupon(Coupon coupon);

    @Select("SELECT COUNT(*) > 0 FROM coupon " +
            "WHERE id = #{couponId} " +
            "AND user_id = #{userId} " +
            "AND expiration_date > NOW() ")
    boolean isCouponValid(@Param("userId") Long userId,
                          @Param("couponId") Long couponId);

    @Update("UPDATE coupon SET is_used = 1 WHERE id = #{couponId}")
    int markAsUsed(Long couponId);

    @Select("SELECT * FROM coupon WHERE type = #{type} AND (user_id IS NULL OR user_id = 0) LIMIT 1")
    Coupon selectAvailableCouponByType(@Param("type") Integer type);

    @Update("UPDATE coupon SET user_id = #{userId} WHERE id = #{id}")
    int updateUserCoupon(@Param("id") Long id, @Param("userId") Long userId);

    @Select("SELECT COUNT(1) FROM coupon WHERE id = #{couponId} AND user_id = #{userId}")
    boolean existsUserCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);
}
