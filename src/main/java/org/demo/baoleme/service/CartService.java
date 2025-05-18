package org.demo.baoleme.service;

import org.demo.baoleme.dto.request.*;
import org.demo.baoleme.dto.response.CartViewResponse;

public interface CartService {
    boolean addToCart(Long userId, AddToCartRequest request);
    CartViewResponse viewCart(Long userId);
    boolean updateCartItem(Long userId, UpdateCartRequest request);
    boolean removeCartItems(Long userId, RemoveCartRequest request);
}