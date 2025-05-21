package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.dto.response.user.*;
import org.demo.baoleme.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT id, username, password, phone, avatar, created_at " +
            "FROM user WHERE id = #{id} LIMIT 1")
    User selectById(Long id);



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

    @Select("SELECT id, username, password, phone, gender, avatar " +
            "FROM user WHERE phone = #{phone} LIMIT 1")
    User selectByPhone(String phone);

    @Select("SELECT COUNT(*) > 0 FROM favorite WHERE user_id = #{userId} AND store_id = #{storeId}")
    boolean existsFavorite(Long userId, Long storeId);

    @Insert("INSERT INTO favorite(user_id, store_id) VALUES(#{userId}, #{storeId})")
    int insertFavorite(Long userId, Long storeId);

    @Select("""
    SELECT store_id
    FROM favorite f 
    WHERE f.user_id = #{userId}
""")
    List<UserFavoriteResponse> selectFavoriteStoresByUserId(Long userId);


    @Select("SELECT (SELECT COUNT(*) FROM product WHERE name LIKE CONCAT('%', #{keyword}, '%')) + " +
            "(SELECT COUNT(*) FROM store WHERE name LIKE CONCAT('%', #{keyword}, '%')) as total")
    int countSearchResults(String keyword);

    @Select("SELECT id as store_id, name as store_name, description, rating, image " +
            "FROM store WHERE description = #{type} OR #{type} IS NULL " +
            "LIMIT #{size} OFFSET #{offset}")
    List<UserGetShopResponse.Shop> selectShopsByType(@Param("type") String type,
                                                     @Param("offset") int offset,
                                                     @Param("size") int size);

    @Select("SELECT COUNT(*) FROM store WHERE description = #{description} OR #{description} IS NULL")
    int countShopsByType(String type);

    @Select("SELECT id as product_id, name as product_name, price, stock, image " +
            "FROM product " +
            "WHERE (store_id = #{shopId} OR #{shopId} IS NULL) " +
            "AND (category = #{category} OR #{category} IS NULL)")
    List<UserGetProductResponse.Product> selectProducts(@Param("storeId") Long storeId,
                                                        @Param("category") String category);

    @Select("SELECT COUNT(*) > 0 FROM store WHERE id = #{storeId}")
    boolean existsShop(Long storeId);

    @Select("SELECT COUNT(*) > 0 FROM product WHERE id = #{productId}")
    boolean existsProduct(Long productId);

}
