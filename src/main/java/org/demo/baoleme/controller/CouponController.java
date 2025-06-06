package org.demo.baoleme.controller;

import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.coupon.CouponCreateRequest;
import org.demo.baoleme.dto.response.coupon.CouponCreateResponse;
import org.demo.baoleme.dto.response.store.StoreCreateResponse;
import org.demo.baoleme.pojo.Coupon;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.service.CouponService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;

    @PostMapping("/create")
    public CommonResponse createConpon(
            @RequestBody CouponCreateRequest request
    ) {
        System.out.println("收到创建请求: " + request);

        // Step1: 从上下文获取商户ID
        Long merchantId = UserHolder.getId();

        // Step2: 初始化实体对象
        Coupon coupon = new Coupon();

        // Step3: 复制请求参数到领域模型
        BeanUtils.copyProperties(request, coupon);
        coupon.setUserId(0L);

        // Step4: 调用服务层创建店铺
        Coupon createdCoupon = couponService.createCoupon(coupon);

        // Step5: 处理创建失败场景
        if (createdCoupon == null) {
            System.out.println("[WARN]: 创建失败");
            return ResponseBuilder.fail("店铺创建失败，参数校验不通过");
        }

        // Step6: 构建响应数据
        CouponCreateResponse response = new CouponCreateResponse();
        response.setCouponId(createdCoupon.getId());

        System.out.println("创建成功，响应: " + response);
        return ResponseBuilder.ok(response);
    }
}
