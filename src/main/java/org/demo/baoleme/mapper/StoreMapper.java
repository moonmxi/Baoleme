package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.dto.response.user.UserGetProductResponse;
import org.demo.baoleme.pojo.Product;
import org.demo.baoleme.pojo.Store;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface StoreMapper extends BaseMapper<Store> {

    @Select("""
    SELECT *
    FROM store
    WHERE (#{keyword} IS NULL OR name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))
      AND (#{type} IS NULL OR type = #{type})
      AND (#{status} IS NULL OR status = #{status})
      AND (#{startRating} IS NULL OR rating >= #{startRating})
      AND (#{endRating} IS NULL OR rating <= #{endRating})
    ORDER BY id DESC
    LIMIT #{offset}, #{limit}
""")
    List<Store> selectStoresPaged(@Param("offset") int offset,
                                  @Param("limit") int limit,
                                  @Param("keyword") String keyword,
                                  @Param("type") String type,
                                  @Param("status") Integer status,
                                  @Param("startRating") BigDecimal startRating,
                                  @Param("endRating") BigDecimal endRating);

    @Delete("DELETE FROM store WHERE name = #{storeName}")
    int deleteByName(@Param("storeName") String storeName);

    /**
     * 搜索匹配店铺名的店铺信息
     * 返回字段：store_id（Long）, store_name（String）
     */
    @Select("""
        SELECT id AS store_id, name AS store_name
        FROM store
        WHERE name LIKE CONCAT('%', #{keyword}, '%')
        """)
    List<Map<String, Object>> searchStoresByKeyword(@Param("keyword") String keyword);
    /**
     * 搜索匹配商品名的商品及其店铺信息
     * 返回字段：store_id（Long）, store_name（String）, product_id（Long）, product_name（String）
     */
    @Select("""
    SELECT s.id AS store_id, s.name AS store_name,
           p.id AS product_id, p.name AS product_name
    FROM product p
    JOIN store s ON p.store_id = s.id
    WHERE p.name LIKE CONCAT('%', #{keyword}, '%')
""")
    List<Map<String, Object>> searchProductsByKeyword(@Param("keyword") String keyword);

    @Select("""
    SELECT id, name, description, type, location, rating, status, created_at, image
    FROM store
    WHERE (#{type} IS NULL OR type = #{type})
    ORDER BY id DESC
""")
    List<Store> selectShopsByType(@Param("type") String type);

    @Select("""
    SELECT COUNT(*) 
    FROM store 
    WHERE (#{type} IS NULL OR type = #{type})
""")
    Integer countShopsByType(@Param("type") String type);

    @Select("""
    SELECT 
        id, 
        store_id, 
        name, 
        category, 
        price, 
        description, 
        image,
        stock,          -- 新增库存字段
        rating,         -- 新增评分字段
        status,         -- 新增状态字段
        created_at      -- 新增创建时间字段
    FROM product
    WHERE store_id = #{storeId}
      AND (#{category} IS NULL OR category = #{category})
    ORDER BY id DESC
""")
    List<UserGetProductResponse> selectProducts(
            @Param("storeId") Long storeId,
            @Param("category") String category
    );

    @Select("SELECT id FROM store WHERE name = #{name}")
    Long getIdByName(@Param("name") String name);

    @Select("SELECT name FROM store WHERE id = #{storeId}")
    String getNameById(@Param("storeId") Long storeId);



    @Update("UPDATE store SET image = #{imagePath} WHERE id = #{storeId}")
    int updateImageById(@Param("storeId") Long storeId, @Param("imagePath") String imagePath);

}
