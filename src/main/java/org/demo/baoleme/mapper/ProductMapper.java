package org.demo.baoleme.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.demo.baoleme.pojo.Product;

@Mapper
public interface ProductMapper {
    @Delete("""
    DELETE FROM product 
    WHERE name = #{productName} 
    AND store_id = (SELECT id FROM store WHERE name = #{storeName} LIMIT 1)
""")
    int deleteByNameAndStore(@Param("productName") String productName,
                             @Param("storeName") String storeName);
}
