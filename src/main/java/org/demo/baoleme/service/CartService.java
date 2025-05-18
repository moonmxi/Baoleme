package org.demo.baoleme.service;

import org.demo.baoleme.dto.request.cart.AddToCartRequest;
import org.demo.baoleme.dto.request.cart.RemoveCartRequest;
import org.demo.baoleme.dto.request.cart.UpdateCartRequest;
import org.demo.baoleme.dto.response.cart.CartViewResponse;

public interface CartService {
    boolean addToCart(Long userId, AddToCartRequest request);
    CartViewResponse viewCart(Long userId);
    boolean updateCartItem(Long userId, UpdateCartRequest request);
    boolean removeCartItems(Long userId, RemoveCartRequest request);
}