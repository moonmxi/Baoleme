package org.demo.baoleme.service.impl;

import lombok.RequiredArgsConstructor;
import org.demo.baoleme.dto.request.cart.AddToCartRequest;
import org.demo.baoleme.dto.request.cart.DeleteCartRequest;
import org.demo.baoleme.dto.request.cart.UpdateCartRequest;
import org.demo.baoleme.dto.response.cart.CartResponse;
import org.demo.baoleme.dto.response.cart.CartViewResponse;
import org.demo.baoleme.mapper.CartMapper;
import org.demo.baoleme.pojo.Cart;
import org.demo.baoleme.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;

    @Override
    @Transactional
    public void addToCart(Long userId, AddToCartRequest request) {
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0");
        }

        Cart existingItem = cartMapper.findByUserIdAndProductId(userId, request.getProductId());

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            existingItem.setQuantity(newQuantity);
            cartMapper.updateById(existingItem);
        } else {
            Cart newItem = new Cart();
            newItem.setUserId(userId);
            newItem.setProductId(request.getProductId());
            newItem.setQuantity(request.getQuantity());
            cartMapper.insert(newItem);
        }
    }

    @Override
    public CartViewResponse viewCart(Long userId) {
        List<CartResponse> items = cartMapper.findCartsByUserId(userId);
        BigDecimal totalPrice = calculateTotalPrice(items);

        CartViewResponse response = new CartViewResponse();
        response.setItems(items);
        response.setTotalPrice(totalPrice);
        return response;
    }

    @Override
    @Transactional
    public boolean updateCart(Long userId, UpdateCartRequest request) {
        Cart item = cartMapper.findByUserIdAndProductId(userId, request.getProductId());
        if (item == null) {
            return false;
        }
        return cartMapper.updateQuantity(userId, request.getProductId(), request.getQuantity()) > 0;
    }

    @Override
    public void deleteCartItem(Long userId, DeleteCartRequest request) {
        // 1. 设置该项的数量为 0
        Long productId = request.getProductId();
//        int rows = cartMapper.updateToZero(userId, productId);
//        System.out.println("更新行数：" + rows);
//
//        // 2. 删除所有 quantity 为 0 的购物车项
//        cartMapper.deleteCartItemByUser(userId);

        cartMapper.deleteCartItemById(userId, productId);

    }


    @Override
    @Transactional
    public void removeCart(Long userId) {
        cartMapper.deleteByUserId(userId);  // 清空购物车

    }

    private BigDecimal calculateTotalPrice(List<CartResponse> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<CartResponse> getCartItemsByUserId(Long userId) {
        return cartMapper.findCartsByUserId(userId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartMapper.deleteByUserId(userId);
    }

}