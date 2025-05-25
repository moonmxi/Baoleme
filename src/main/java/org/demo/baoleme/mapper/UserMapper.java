package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.dto.response.user.*;
import org.demo.baoleme.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /*
    @Select("SELECT id, username, password, phone, avatar, created_at, location, description, gender " +
            "FROM user WHERE id = #{id} LIMIT 1")
    User selectById(Long id);
    */

    @Delete("DELETE FROM user WHERE username = #{username}")
    int deleteByUsername(@Param("username") String username);


    @Select("""
    SELECT id, username, phone, avatar, created_at
    FROM user
    ORDER BY created_at DESC
    LIMIT #{offset}, #{limit}
""")
    List<User> selectUsersPaged(@Param("offset") int offset,
                                @Param("limit") int limit);

    @Select("SELECT id, username, password, phone, gender, avatar " +
            "FROM user WHERE username = #{username} LIMIT 1")
    User selectByUsername(String username);

    @Select("SELECT id, username, password, gender, avatar, description, location, created_at " +
            "FROM user WHERE phone = #{phone} LIMIT 1")
    User selectByPhone(String phone);

    @Select("SELECT COUNT(*) > 0 FROM favorite WHERE user_id = #{userId} AND store_id = #{storeId}")
    boolean existsFavorite(Long userId, Long storeId);

    @Insert("INSERT INTO favorite(user_id, store_id) VALUES(#{userId}, #{storeId})")
    int insertFavorite(Long userId, Long storeId);

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
""")
    List<UserFavoriteResponse> selectFavoriteStoresWithDetails(Long userId);

    @Select("SELECT (SELECT COUNT(*) FROM product WHERE name LIKE CONCAT('%', #{keyword}, '%')) + " +
            "(SELECT COUNT(*) FROM store WHERE name LIKE CONCAT('%', #{keyword}, '%')) as total")
    int countSearchResults(String keyword);



    @Select("SELECT COUNT(*) FROM store WHERE description = #{description} OR #{description} IS NULL")
    int countShopsByType(String type);


    @Select("SELECT COUNT(*) > 0 FROM store WHERE id = #{storeId}")
    boolean existsShop(Long storeId);

    @Select("SELECT COUNT(*) > 0 FROM product WHERE id = #{productId}")
    boolean existsProduct(Long productId);

}
