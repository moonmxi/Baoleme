package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.dto.response.cart.CartItemResponse;
import org.demo.baoleme.pojo.CartItem;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<CartItem> {

    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    CartItem findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("SELECT c.*, p.name as product_name, p.price, p.image " +
            "FROM cart c JOIN product p ON c.product_id = p.id " +
            "WHERE c.user_id = #{userId}")
    List<CartItemResponse> findCartItemsByUserId(Long userId);

    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND product_id IN " +
            "<foreach item='id' collection='productIds' open='(' separator=',' close=')'>#{id}</foreach>")
    int deleteByUserIdAndProductIds(@Param("userId") Long userId, @Param("productIds") List<Long> productIds);
}