package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.demo.baoleme.pojo.Product;

import java.util.List;

public interface ProductMapper extends BaseMapper<Product> {
    @Update("UPDATE product SET stock = stock - #{quantity} WHERE id = #{productId} AND stock >= #{quantity}")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    List<Product> searchProductsByName(String s, int offset, int size);

    Long countProductsByName(String s);

    @Select("SELECT id FROM product WHERE name = #{name} AND store_id = #{storeId}")
    Long getIdByNameAndStoreId(@Param("name") String name, @Param("storeId") Long storeId);

    @Select("SELECT name FROM product WHERE id = #{id}")
    String getNameById();
}
