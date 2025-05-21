package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.pojo.Store;

import java.util.List;
import java.util.Map;

@Mapper
public interface StoreMapper extends BaseMapper<Store> {

    @Select("""
    SELECT id, name, description, location, rating, status, created_at, image
    FROM store
    ORDER BY id DESC
    LIMIT #{offset}, #{limit}
""")
    List<Store> selectStoresPaged(@Param("offset") int offset, @Param("limit") int limit);

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

    /**
     * 更新店铺信息
     * @param store 店铺对象
     * @return 受影响的行数
     */
    @Update("UPDATE store SET name=#{name}, `description`=#{description}, location=#{location}, rating=#{rating}, balance=#{balance}, status=#{status}, image=#{image}, merchant_id=#{merchantId} WHERE id = #{id}")
    int updateStore(Store store);

}
