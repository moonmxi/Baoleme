package org.demo.baoleme.controller;

import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.store.StoreCreateRequest;
import org.demo.baoleme.dto.request.store.StoreDeleteRequest;
import org.demo.baoleme.dto.request.store.StoreUpdateRequest;
import org.demo.baoleme.dto.request.store.StoreViewInfoRequest;
import org.demo.baoleme.dto.response.store.StoreCreateResponse;
import org.demo.baoleme.dto.response.store.StoreUpdateResponse;
import org.demo.baoleme.dto.response.store.StoreViewInfoResponse;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.service.StoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping("/create")
    public CommonResponse createStore(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StoreCreateRequest request
    ) {
        System.out.println("收到创建请求: " + request); // 错误修正：更新->创建

        // Step1: 获取商户ID
        Long merchantId = UserHolder.getId();

        // Step2: 初始化店铺对象
        Store store = new Store();
        store.setMerchantId(merchantId);

        // Step3: 拷贝请求参数
        BeanUtils.copyProperties(request, store);

        // Step4: 执行创建操作
        Store createdStore = storeService.createStore(store);

        // Step5: 处理创建结果
        if (createdStore == null) {
            System.out.println("创建失败，店铺ID: " + request.getName());
            return ResponseBuilder.fail("店铺创建失败，参数校验不通过");
        }

        // Step6: 构造响应体
        StoreCreateResponse response = new StoreCreateResponse();
        BeanUtils.copyProperties(createdStore, response);

        System.out.println("创建成功，响应: " + response);
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/view")
    public CommonResponse getStoreById(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StoreViewInfoRequest request
    ) {
        System.out.println("收到查询请求: " + request);

        // Step1: 提取店铺ID
        Long storeId = request.getStoreId();

        if (!validateStoreOwnership(storeId)) return ResponseBuilder.fail("店铺不属于您");

        // Step2: 执行数据库查询
        Store store = storeService.getStoreById(storeId);

        // Step3: 处理空结果
        if (store == null) {
            System.out.println("查询失败，无效店铺ID: " + storeId);
            return ResponseBuilder.fail("店铺ID不存在");
        }

        // Step4: 数据转换
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

        // Step1: 初始化领域对象
        Store store = new Store();
        store.setId(request.getId());

        if (!validateStoreOwnership(store.getId())) return ResponseBuilder.fail("店铺不属于您");

        // Step2: 拷贝更新字段
        BeanUtils.copyProperties(request, store);

        // Step3: 执行更新操作
        boolean success = storeService.updateStore(store);

        // Step4: 处理失败场景
        if (!success) {
            System.out.println("更新失败，店铺ID: " + request.getId());
            return ResponseBuilder.fail("店铺信息更新失败");
        }

        // Step5: 构造响应数据
        StoreUpdateResponse response = new StoreUpdateResponse();
        BeanUtils.copyProperties(store, response);

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

        if (!validateStoreOwnership(storeId)) return ResponseBuilder.fail("店铺不属于您");

        // Step2: 校验状态值
        if (status < 0 || status > 1) {
            System.out.println("非法状态值: " + status);
            return ResponseBuilder.fail("状态值必须是0或1");
        }

        // Step3: 执行状态修改
        boolean success = storeService.toggleStoreStatus(storeId, status);

        // Step4: 返回操作结果
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

        if (!validateStoreOwnership(storeId)) return ResponseBuilder.fail("店铺不属于您");

        // Step2: 执行删除操作
        boolean success = storeService.deleteStore(storeId);

        // Step3: 处理操作结果
        System.out.println(success ? "删除成功" : "删除失败");
        return success ?
                ResponseBuilder.ok("店铺数据已删除") :
                ResponseBuilder.fail("删除操作失败，店铺可能不存在");
    }

    private boolean validateStoreOwnership(Long storeId) {
        Long merchantId = UserHolder.getId();
        return storeService.validateStoreOwnership(storeId, merchantId);
    }
}