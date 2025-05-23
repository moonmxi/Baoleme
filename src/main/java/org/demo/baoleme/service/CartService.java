package org.demo.baoleme.service;

import org.demo.baoleme.dto.request.cart.AddToCartRequest;
import org.demo.baoleme.dto.request.cart.DeleteCartRequest;

import org.demo.baoleme.dto.request.cart.UpdateCartRequest;
import org.demo.baoleme.dto.response.cart.CartResponse;
import org.demo.baoleme.dto.response.cart.CartViewResponse;

import java.util.List;

public interface CartService {
    void addToCart(Long userId, AddToCartRequest request);
    CartViewResponse viewCart(Long userId);
    boolean updateCart(Long userId, UpdateCartRequest request);

    void deleteCartItem(Long userId, DeleteCartRequest request);

    void removeCart(Long userId);

    List<CartResponse> getCartItemsByUserId(Long userId);

    void clearCart(Long userId);
}