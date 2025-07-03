package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.dto.response.user.*;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.pojo.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Delete("DELETE FROM user WHERE username = #{username}")
    int deleteByUsername(@Param("username") String username);


    @Select("""
    SELECT *
    FROM user
    WHERE (#{keyword} IS NULL OR username LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))
      AND (#{gender} IS NULL OR gender = #{gender})
      AND (#{startId} IS NULL OR id >= #{startId})
      AND (#{endId} IS NULL OR id <= #{endId})
    ORDER BY created_at DESC
    LIMIT #{offset}, #{limit}
""")
    List<User> selectUsersPaged(@Param("keyword") String keyword,
                                @Param("gender") String gender,
                                @Param("startId") Long startId,
                                @Param("endId") Long endId,
                                @Param("offset") int offset,
                                @Param("limit") int limit);

    @Select("SELECT * " +
            "FROM user WHERE username = #{username} LIMIT 1")
    User selectByUsername(String username);

    @Select("SELECT * " +
            "FROM user WHERE phone = #{phone} LIMIT 1")
    User selectByPhone(String phone);

    @Select("SELECT COUNT(*) > 0 FROM favorite WHERE user_id = #{userId} AND store_id = #{storeId}")
    boolean existsFavorite(Long userId, Long storeId);

    @Insert("INSERT INTO favorite(user_id, store_id) VALUES(#{userId}, #{storeId})")
    int insertFavorite(Long userId, Long storeId);

    @Update("UPDATE user SET username = #{username},password = #{password},phone = #{phone} ,avatar = #{avatar} " +
            ", description = #{description} , location = #{location} , gender = #{gender} WHERE user_id = #{userId}")
    int updateUser(Long userId, String username, String password, String phone, String avatar, String description, String location, String gender);
    
    @Select("""
    SELECT 
        f.store_id, 
        s.name, 
        s.description, 
        s.location, 
        s.type, 
        s.rating, 
        s.status, 
        s.created_at AS createdAt,
        s.image
    FROM favorite f 
    INNER JOIN store s ON f.store_id = s.id
    WHERE f.user_id = #{userId}
        AND (#{type} IS NULL OR s.type = #{type})
        AND (#{distance} IS NULL OR s.distance <= #{distance})
        AND (#{startRating} IS NULL OR s.rating >= #{startRating})
        AND (#{endRating} IS NULL OR s.rating <= #{endRating})
        AND (#{avg_price} IS NULL OR s.avg_price <= #{avg_price})
    ORDER BY s.id DESC
    LIMIT #{offset}, #{limit}
""")
    List<UserFavoriteResponse> selectFavoriteStoresWithDetails(
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("distance") BigDecimal distance,
            @Param("avg_price") BigDecimal avgPrice,
            @Param("startRating") BigDecimal startRating,
            @Param("endRating") BigDecimal endRating,
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Select("""
    SELECT 
        s.id AS store_id, 
        s.name, 
        s.description, 
        s.location, 
        s.type, 
        s.rating, 
        s.status, 
        s.created_at AS createdAt,
        s.image
    FROM store s
    WHERE 1=1
        AND (#{type} IS NULL OR s.type = #{type})
        AND (#{distance} IS NULL OR s.distance <= #{distance})
        AND (#{startRating} IS NULL OR s.rating >= #{startRating})
        AND (#{endRating} IS NULL OR s.rating <= #{endRating})
        AND (#{avg_price} IS NULL OR s.avg_price <= #{avg_price})
    LIMIT #{offset}, #{limit}
""")
    List<UserFavoriteResponse> getStores(
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("distance") BigDecimal distance,
            @Param("avg_price") BigDecimal avgPrice,
            @Param("startRating") BigDecimal startRating,
            @Param("endRating") BigDecimal endRating,
            @Param("offset") int offset,
            @Param("limit") int limit);


    @Update("UPDATE user SET avatar = #{avatarPath} WHERE id = #{userId}")
    int updateAvatarById(@Param("userId") Long userId, @Param("avatarPath") String avatarPath);

    @Select("""
    SELECT 
        s.id AS store_id, 
        s.name, 
        s.description, 
        s.location, 
        s.type, 
        s.rating, 
        s.status, 
        s.created_at AS createdAt,
        s.image
    FROM store s
    WHERE 1=1
        AND (#{keyword} IS NULL OR s.name LIKE CONCAT('%', #{keyword}, '%') OR s.description LIKE CONCAT('%', #{keyword}, '%') OR s.type LIKE CONCAT('%', #{keyword}, '%'))
        AND (#{distance} IS NULL OR s.distance <= #{distance})
        AND (#{startRating} IS NULL OR s.rating >= #{startRating})
        AND (#{endRating} IS NULL OR s.rating <= #{endRating})
        AND (#{avg_price} IS NULL OR s.avg_price <= #{avg_price})
    LIMIT #{offset}, #{limit}
""")
    List<UserSearchResponse> searchStores(@Param("keyword") String keyword,
                                           @Param("distance") BigDecimal distance,
                                           @Param("avg_price") BigDecimal averagePrice,
                                           @Param("startRating") BigDecimal startRating,
                                           @Param("endRating") BigDecimal endRating,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    @Update("INSERT INTO browse_history(user_id, store_id, created_at) " +
            "VALUES(#{userId}, #{storeId}, #{viewTime}) " +
            "ON DUPLICATE KEY UPDATE created_at = #{viewTime}")
    int addViewHistory(Long userId, Long storeId, LocalDateTime viewTime);

    @Select("SELECT s.* FROM store s " +
            "INNER JOIN browse_history bh ON s.id = bh.store_id " +
            "WHERE bh.user_id = #{userId} " +
            "ORDER BY bh.created_at DESC " +  // 按浏览时间倒序
            "LIMIT #{offset}, #{pageSize}")
    List<Store> selectViewHistory(@Param("userId") Long userId,
                                  @Param("offset") int offset,
                                  @Param("pageSize") Integer pageSize);

    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND store_id = #{storeId}")
    int deleteFavorite(Long userId, Long storeId);
}
