package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.dto.response.user.UserSearchOrderItemResponse;
import org.demo.baoleme.pojo.Product;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    @Delete("""
    DELETE FROM product 
    WHERE name = #{productName} 
    AND store_id = (SELECT id FROM store WHERE name = #{storeName} LIMIT 1)
""")
    int deleteByNameAndStore(@Param("productName") String productName,
                             @Param("storeName") String storeName);

    // 分页查询方法
    @Select("""
        SELECT * FROM product 
        WHERE store_id = #{storeId} 
        LIMIT #{pageSize} OFFSET #{offset}
    """)
    List<Product> selectByStore(
            @Param("storeId") Long storeId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    // 总数统计方法
    @Select("SELECT COUNT(*) FROM product WHERE store_id = #{storeId}")
    int countByStore(@Param("storeId") Long storeId);

    @Update("UPDATE product SET stock = stock - #{quantity} WHERE id = #{productId} AND stock >= #{quantity}")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Select("SELECT id FROM product WHERE name = #{name} AND store_id = #{storeId}")
    Long getIdByNameAndStoreId(@Param("name") String name, @Param("storeId") Long storeId);

    @Select("SELECT name FROM product WHERE id = #{productId}")
    String getNameById(@Param("productId") Long productId);

    @Update("UPDATE product SET image = #{imagePath} WHERE id = #{productId}")
    int updateImageById(@Param("productId") Long productId, @Param("imagePath") String imagePath);

    @Select("SELECT p.*, o.quantity " +
            "FROM product p JOIN order_item o ON p.id = o.product_id " +
            "WHERE o.order_id = #{orderId}")
    List<UserSearchOrderItemResponse> selectByOrderId(Long orderId);

    /**
     * 每个商品的总量
     * @param productId
     * @return
     */
    @Select(
            """
        SELECT COALESCE(SUM(quantity), 0)
        FROM sales
        WHERE product_id = #{productId}
            """
    )
    int getProductVolume(@Param("productId") Long productId);
}
