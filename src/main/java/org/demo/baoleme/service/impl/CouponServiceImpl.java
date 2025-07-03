package org.demo.baoleme.service.impl;

import org.demo.baoleme.mapper.*;
import org.demo.baoleme.pojo.Coupon;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouponServiceImpl implements CouponService {
    @Autowired
    private StoreMapper storeMapper;
    @Autowired
    private CouponMapper couponMapper;

    @Override
    public Coupon createCoupon(Coupon coupon){
        // Step1: 校验...是否为空


        // Step2: 检查store_id是否存在
        Store store = storeMapper.selectById(coupon.getStoreId());
        if (store == null) {
            System.out.println("[WARN]：店铺ID不存在");
            return null;
        }

        // Step3: 插入商品数据
        int result = couponMapper.insert(coupon);
        return result > 0 ? coupon : null;
    }
}
