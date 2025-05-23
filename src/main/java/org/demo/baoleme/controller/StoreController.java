package org.demo.baoleme.controller;

import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.dto.request.user.UserGetProductRequest;
import org.demo.baoleme.dto.request.user.UserGetShopRequest;
import org.demo.baoleme.dto.response.user.UserGetProductResponse;
import org.demo.baoleme.dto.response.user.UserGetShopResponse;
import org.demo.baoleme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stores")
public class StoreController {

    @Autowired
    private UserService userService;

    // 商家浏览
    @GetMapping
    public CommonResponse getShops(@RequestParam(required = false) String description) {
        UserGetShopResponse response = userService.getStoresByDescription(description);
        return ResponseBuilder.ok(response);
    }

    // 商品浏览
    @GetMapping("/{storeId}/products")
    public CommonResponse getProductsByStore(
            @PathVariable Long storeId,
            @RequestParam(required = false) String category) {
        UserGetProductResponse response = userService.getProducts(storeId, category);
        return ResponseBuilder.ok(response);
    }
}
