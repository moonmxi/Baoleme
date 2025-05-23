package org.demo.baoleme.controller;

import lombok.RequiredArgsConstructor;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.cart.AddToCartRequest;
import org.demo.baoleme.dto.request.cart.DeleteCartRequest;

import org.demo.baoleme.dto.request.cart.UpdateCartRequest;
import org.demo.baoleme.dto.response.cart.CartViewResponse;
import org.demo.baoleme.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public CommonResponse addToCart(@RequestBody AddToCartRequest request) {
        cartService.addToCart(UserHolder.getId(), request);
        return ResponseBuilder.ok();
    }

    @GetMapping("/view")
    public CommonResponse viewCart() {
        CartViewResponse response = cartService.viewCart(UserHolder.getId());
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/update")
    public CommonResponse updateCartItem(@RequestBody UpdateCartRequest request) {
        cartService.updateCart(UserHolder.getId(), request);
        return ResponseBuilder.ok();
    }

    @PutMapping("/delete")
    public CommonResponse deleteCartItem(@RequestBody DeleteCartRequest request) {

        cartService.deleteCartItem(UserHolder.getId(), request);
        //cartService.deleteCartItem(10000004L, request);
        return ResponseBuilder.ok();
    }

    @DeleteMapping("/remove")
    public CommonResponse removeCartItems() {
        cartService.removeCart(UserHolder.getId());
        return ResponseBuilder.ok();
    }
}