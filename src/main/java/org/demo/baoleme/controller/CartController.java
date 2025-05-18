package org.demo.baoleme.controller;

import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.cart.AddToCartRequest;
import org.demo.baoleme.dto.request.cart.RemoveCartRequest;
import org.demo.baoleme.dto.request.cart.UpdateCartRequest;
import org.demo.baoleme.dto.response.cart.CartViewResponse;
import org.demo.baoleme.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public CommonResponse addToCart( @RequestBody AddToCartRequest request) {
        Long userId = UserHolder.getId();
        boolean success = cartService.addToCart(userId, request);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("添加购物车失败");
    }

    @GetMapping("/view")
    public CommonResponse viewCart() {
        Long userId = UserHolder.getId();
        CartViewResponse response = cartService.viewCart(userId);
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/update")
    public CommonResponse updateCartItem( @RequestBody UpdateCartRequest request) {
        Long userId = UserHolder.getId();
        boolean success = cartService.updateCartItem(userId, request);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("更新购物车失败");
    }

    @DeleteMapping("/remove")
    public CommonResponse removeCartItems( @RequestBody RemoveCartRequest request) {
        Long userId = UserHolder.getId();
        boolean success = cartService.removeCartItems(userId, request);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("删除购物车商品失败");
    }
}