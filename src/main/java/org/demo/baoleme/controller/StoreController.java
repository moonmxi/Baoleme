package org.demo.baoleme.controller;

import org.demo.baoleme.common.*;
import org.demo.baoleme.dto.request.store.*;
import org.demo.baoleme.dto.request.user.UserGetFavoriteStoresRequest;
import org.demo.baoleme.dto.request.user.UserGetProductByConditionRequest;
import org.demo.baoleme.dto.response.product.ProductViewResponse;
import org.demo.baoleme.dto.response.store.*;
import org.demo.baoleme.dto.response.user.UserFavoriteResponse;
import org.demo.baoleme.dto.response.user.UserGetProductResponse;
import org.demo.baoleme.dto.response.user.UserGetShopResponse;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.service.StoreService;
import org.demo.baoleme.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    @Autowired
    private UserService userService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping("/create")
    public CommonResponse createStore(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StoreCreateRequest request
    ) {
        System.out.println("收到创建请求: " + request);

        // Step1: 从上下文获取商户ID
        Long merchantId = UserHolder.getId();

        // Step2: 初始化店铺实体对象
        Store store = new Store();
        store.setMerchantId(merchantId);

        // Step3: 复制请求参数到领域模型
        BeanUtils.copyProperties(request, store);

        // Step4: 调用服务层创建店铺
        Store createdStore = storeService.createStore(store);

        // Step5: 处理创建失败场景
        if (createdStore == null) {
            System.out.println("创建失败，店铺名称: " + request.getName());
            return ResponseBuilder.fail("店铺创建失败，参数校验不通过");
        }

        // Step6: 构建响应数据
        StoreCreateResponse response = new StoreCreateResponse();
        response.setId(createdStore.getId());

        System.out.println("创建成功，响应: " + response);
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/list")
    public CommonResponse listStore(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StoreListRequest request
    ){
        System.out.println(
                "收到查询请求: " + request
//                + "查询者: " + UserHolder.getId()
        );

        List<Store> stores = storeService.getStoresByMerchant(
                UserHolder.getId(),
                request.getPage(),
                request.getPageSize()
        );

        List<StoreViewInfoResponse> storeViewInfoResponses = stores.stream()
                .map(store -> {
                    StoreViewInfoResponse resp = new StoreViewInfoResponse();
                    BeanUtils.copyProperties(store, resp);
                    resp.setStoreId(store.getId());
                    return resp;
                })
                .collect(Collectors.toList());

        StorePageResponse response = new StorePageResponse();
        response.setStores(storeViewInfoResponses);
        response.setCurrentPage(request.getPage());

        System.out.println("[INFO] 已应答");
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/view")
    public CommonResponse getStoreById(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StoreViewInfoRequest request
    ) {
        System.out.println("收到查询请求: " + request);

        // Step1: 提取请求中的店铺ID
        Long storeId = request.getStoreId();

        // Step2: 验证店铺归属权
        if (!validateStoreOwnership(storeId)) return ResponseBuilder.fail("店铺不属于您");

        // Step3: 查询店铺详细信息
        Store store = storeService.getStoreById(storeId);

        // Step4: 处理无效店铺ID场景
        if (store == null) {
            System.out.println("查询失败，无效店铺ID: " + storeId);
            return ResponseBuilder.fail("店铺ID不存在");
        }

        // Step5: 转换领域模型为响应对象
        StoreViewInfoResponse response = new StoreViewInfoResponse();
        BeanUtils.copyProperties(store, response);

        System.out.println("查询成功，店铺信息: " + response);
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/update")
    public CommonResponse updateStore(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StoreUpdateRequest request
    ) {
        System.out.println("收到更新请求: " + request);

        // Step1: 初始化待更新的店铺对象
        Long storeId = request.getId();
        Store store = new Store();
        store.setId(storeId);

        // Step2: 验证店铺操作权限
        if (!validateStoreOwnership(store.getId())) return ResponseBuilder.fail("店铺不属于您");

        // Step3: 复制更新字段到领域模型
        BeanUtils.copyProperties(request, store);

        // Step4: 执行数据库更新操作
        boolean success = storeService.updateStore(store);

        Store newStore = storeService.getStoreById(storeId);

        // Step5: 处理更新失败场景
        if (!success) {
            System.out.println("更新失败，店铺ID: " + request.getId());
            return ResponseBuilder.fail("店铺信息更新失败");
        }

        // Step6: 构建更新响应数据
        StoreUpdateResponse response = new StoreUpdateResponse();
        BeanUtils.copyProperties(newStore, response);

        System.out.println("更新成功，响应: " + response);
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/status")
    public CommonResponse toggleStoreStatus(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StoreUpdateRequest request
    ) {
        System.out.println("收到状态修改请求: " + request);

        // Step1: 解析请求参数
        Long storeId = request.getId();
        int status = request.getStatus();

        // Step2: 验证店铺归属权
        if (!validateStoreOwnership(storeId)) return ResponseBuilder.fail("店铺不属于您");

        // Step3: 校验状态值合法性
        if (status < 0 || status > 1) {
            System.out.println("非法状态值: " + status);
            return ResponseBuilder.fail("状态值必须是0或1");
        }

        // Step4: 执行状态切换操作
        boolean success = storeService.toggleStoreStatus(storeId, status);

        // Step5: 生成操作结果响应
        System.out.println(success ? "状态修改成功" : "状态修改失败");
        return success ?
                ResponseBuilder.ok("店铺状态更新成功") :
                ResponseBuilder.fail("状态更新失败，店铺可能不存在");
    }

    @DeleteMapping("/delete")
    public CommonResponse deleteStore(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StoreDeleteRequest request
    ) {
        System.out.println("收到删除请求: " + request);

        // Step1: 获取目标店铺ID
        Long storeId = request.getStoreId();

        // Step2: 验证删除权限
        if (!validateStoreOwnership(storeId)) return ResponseBuilder.fail("店铺不属于您");

        // Step3: 执行软删除操作
        boolean success = storeService.deleteStore(storeId);

        // Step4: 返回操作结果
        System.out.println(success ? "删除成功" : "删除失败");
        return success ?
                ResponseBuilder.ok("店铺数据已删除") :
                ResponseBuilder.fail("删除操作失败，店铺可能不存在");
    }

    /**
     * Step风格权限验证方法
     * Step1: 从上下文获取当前商户ID
     * Step2: 调用服务层验证店铺归属关系
     */
    private boolean validateStoreOwnership(Long storeId) {
        // Step1: 获取当前操作者ID
        Long merchantId = UserHolder.getId();

        // Step2: 验证店铺与商户的归属关系
        return storeService.validateStoreOwnership(storeId, merchantId);
    }

    // 商家浏览
    @PostMapping("/user-view-stores")
    public CommonResponse getShops(@RequestBody UserGetFavoriteStoresRequest request) {
        Long userId = UserHolder.getId();
        String type = request.getType();
        BigDecimal distance = request.getDistance();
        BigDecimal wishPrice = request.getWishPrice();
        BigDecimal startRating = request.getStartRating();
        BigDecimal endRating = request.getEndRating();
        Integer page = request.getPage();
        Integer pageSize = request.getPageSize();
        //favourite 请求与返回的代码复用
        List<UserFavoriteResponse> stores = userService.getStores(userId,type, distance,wishPrice,startRating,endRating,page,pageSize);

        return ResponseBuilder.ok(stores);
    }

    // 商品浏览
    @PostMapping("/user-view-products")
    public CommonResponse getProductsByStore(@RequestBody UserGetProductByConditionRequest request) {
        Long storeId = request.getStoreId();
        String category = request.getCategory();
        List<UserGetProductResponse> response = userService.getProducts(storeId, category);
        System.out.println(
                "查询成功，店铺信息: " + response
        );
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/storeInfo")
    public CommonResponse getStoreInfo(@RequestBody StoreInfoRequest request) {
        Long id = request.getId();
        Store store = storeService.getStoreById(id);
        StoreInfoResponse response = new StoreInfoResponse();
        BeanUtils.copyProperties(store, response);
        return ResponseBuilder.ok(response);
    }
}
