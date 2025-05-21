package org.demo.baoleme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.demo.baoleme.mapper.StoreMapper;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.service.StoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreMapper storeMapper;

    public StoreServiceImpl(StoreMapper storeMapper) {
        this.storeMapper = storeMapper;
    }

    @Override
    @Transactional
    public Store createStore(Store store) {
        // Step1: 校验必填字段
        if (store.getMerchantId() == null || !StringUtils.hasText(store.getName())) {
            System.out.println("创建失败：商家ID或店铺名为空");
            return null;
        }

        // Step2: 检查店铺名称唯一性
        Long count = storeMapper.selectCount(new LambdaQueryWrapper<Store>()
                .eq(Store::getName, store.getName()));
        if (count > 0) {
            System.out.println("创建失败：店铺名称重复");
            return null;
        }

        // Step3: 调用insert方法
        int rows = storeMapper.insert(store);
        return rows > 0 ? store : null;
    }

    @Override
    @Transactional(readOnly = true)
    public Store getStoreById(Long storeId) {
        return storeMapper.selectById(storeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Store> getStoresByMerchant(Long merchantId) {
        return storeMapper.selectList(new LambdaQueryWrapper<Store>()
                .eq(Store::getMerchantId, merchantId));
    }

    @Override
    @Transactional
    public boolean updateStore(Store store) {
        // Step1: 基础参数校验
        if (store.getId() == null || !StringUtils.hasText(store.getName())) {
            System.out.println("更新失败：参数不合法");
            return false;
        }

        // Step2: 检查店铺存在性
        Store existStore = storeMapper.selectById(store.getId());
        if (existStore == null) {
            System.out.println("更新失败：店铺不存在");
            return false;
        }

        // Step3: 名称修改时校验唯一性
        if (!store.getName().equals(existStore.getName())) {
            Long nameCount = storeMapper.selectCount(new LambdaQueryWrapper<Store>()
                    .eq(Store::getName, store.getName()));
            if (nameCount > 0) {
                System.out.println("更新失败：新店铺名称重复");
                return false;
            }
        }

        // Step4: 执行更新
        return storeMapper.updateById(store) > 0;
    }

    @Override
    @Transactional
    public boolean toggleStoreStatus(Long storeId, int status) {
        // Step1: 参数校验
        if (storeId == null || (status != 0 && status != 1)) {
            System.out.println("状态修改失败：参数不合法");
            return false;
        }

        // Step2: 检查店铺存在性
        Store existStore = storeMapper.selectById(storeId);
        if (existStore == null) {
            System.out.println("状态修改失败：店铺不存在");
            return false;
        }

        // Step3: 构建更新对象
        Store store = new Store();
        store.setId(storeId);
        store.setStatus(status);

        // Step4: 执行更新
        return storeMapper.updateById(store) > 0;
    }

    @Override
    @Transactional
    public boolean deleteStore(Long storeId) {
        // Step1: 检查店铺存在性
        if (storeMapper.selectById(storeId) == null) {
            System.out.println("删除失败：店铺不存在");
            return false;
        }

        // Step2: 执行删除
        return storeMapper.deleteById(storeId) > 0;
    }

    @Override
    @Transactional
    public boolean validateStoreOwnership(Long storeId, Long merchantId){
        // Step1: 传入参数不为空
        if (storeId == null || merchantId == null) {
            System.out.println("error: 无效参数");
            return false;
        }

        // Step2: 数据库能得到有效数据
        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            System.out.println("error: 找不到店铺");
            return false;
        }

        // Step3: 判断merchantId是否相等
        return store.getMerchantId().equals(merchantId);
    }
}