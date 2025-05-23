package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.dto.response.cart.CartResponse;
import org.demo.baoleme.pojo.Cart;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    Cart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("SELECT c.*, p.name as product_name, p.price, p.image " +
            "FROM cart c JOIN product p ON c.product_id = p.id " +
            "WHERE c.user_id = #{userId}")
    List<CartResponse> findCartsByUserId(Long userId);

    @Update("UPDATE cart SET quantity = 0 WHERE user_id = #{userId} AND product_id = #{productId}")
        //@Update("UPDATE cart SET quantity = 0 WHERE user_id = 10000004 AND product_id = 60000002")
    int updateToZero(@Param("userId") Long userId, @Param("productId") Long productId);

    @Delete("DELETE FROM cart WHERE quantity = 0 AND user_id = #{userId}")
    int deleteCartItemByUser(@Param("userId") Long userId);

    @Delete("DELETE FROM cart WHERE product_id = #{productId} AND user_id = #{userId}")
    int deleteCartItemById(@Param("userId") Long userId, @Param("productId") Long productId);

    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    @Update("UPDATE cart SET quantity = #{quantity} WHERE user_id = #{userId} AND product_id = #{productId}")
    int updateQuantity(@Param("userId") Long userId, @Param("productId") Long productId, @Param("quantity") int quantity);

}