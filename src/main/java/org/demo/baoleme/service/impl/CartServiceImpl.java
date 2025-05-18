package org.demo.baoleme.service.impl;

import org.demo.baoleme.dto.request.cart.AddToCartRequest;
import org.demo.baoleme.dto.request.cart.RemoveCartRequest;
import org.demo.baoleme.dto.request.cart.UpdateCartRequest;
import org.demo.baoleme.dto.response.cart.CartItemResponse;
import org.demo.baoleme.dto.response.cart.CartViewResponse;
import org.demo.baoleme.mapper.CartMapper;
import org.demo.baoleme.pojo.CartItem;
import org.demo.baoleme.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    @Transactional
    public boolean addToCart(Long userId, AddToCartRequest request) {
        CartItem existingItem = cartMapper.findByUserIdAndProductId(userId, request.getProductId());

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            return cartMapper.updateById(existingItem) > 0;
        } else {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(request.getProductId());
            newItem.setQuantity(request.getQuantity());
            return cartMapper.insert(newItem) > 0;
        }
    }

    @Override
    public CartViewResponse viewCart(Long userId) {
        List<CartItemResponse> items = cartMapper.findCartItemsByUserId(userId);
        BigDecimal totalPrice = calculateTotalPrice(items);

        CartViewResponse response = new CartViewResponse();
        response.setItems(items);
        response.setTotalPrice(totalPrice);
        return response;
    }

    @Override
    @Transactional
    public boolean updateCartItem(Long userId, UpdateCartRequest request) {
        CartItem item = cartMapper.findByUserIdAndProductId(userId, request.getProductId());
        if (item == null) {
            return false;
        }

        item.setQuantity(request.getQuantity());
        return cartMapper.updateById(item) > 0;
    }

    @Override
    @Transactional
    public boolean removeCartItems(Long userId, RemoveCartRequest request) {
        if (request.getProductIds().isEmpty()) {
            // 清空购物车
            return cartMapper.deleteByUserIdAndProductIds(userId, List.of()) > 0;
        }
        return cartMapper.deleteByUserIdAndProductIds(userId, request.getProductIds()) > 0;
    }

    private BigDecimal calculateTotalPrice(List<CartItemResponse> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}