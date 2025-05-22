package org.demo.baoleme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.demo.baoleme.mapper.StoreMapper;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.service.StoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 店铺服务实现类，提供店铺的增删改查及业务校验功能
 */
@Service
public class StoreServiceImpl implements StoreService {
    private final StoreMapper storeMapper;

    public StoreServiceImpl(StoreMapper storeMapper) {
        this.storeMapper = storeMapper;
    }

    /* ========================= 店铺创建 ========================= */

    /**
     * 创建新店铺
     * @param store 包含商户ID和店铺名称的店铺对象
     * @return 创建成功的店铺对象，失败返回null
     */
    @Override
    @Transactional
    public Store createStore(Store store) {
        // Step1: 参数有效性校验
        if (!validateCreateParams(store)) {
            System.out.println("[WARN] 创建失败：商家ID或店铺名为空");
            return null;
        }

        // Step2: 唯一性校验
        if (isStoreNameExists(store.getName())) {
            System.out.println("[WARN] 创建失败：店铺名称 '" + store.getName() + "' 已存在");
            return null;
        }

        // Step3: 执行持久化
        return storeMapper.insert(store) > 0 ? store : null;
    }

    /* ========================= 店铺查询 ========================= */

    /**
     * 根据店铺ID查询店铺详情
     * @param storeId 店铺唯一标识
     * @return 存在返回店铺对象，否则返回null
     */
    @Override
    @Transactional(readOnly = true)
    public Store getStoreById(Long storeId) {
        // Step1: ID有效性检查
        if (storeId == null) {
            System.out.println("[WARN] 查询失败：店铺ID为空");
            return null;
        }

        // Step2: 执行查询
        return storeMapper.selectById(storeId);
    }

    /**
     * 获取指定商户下的所有店铺列表
     * @param merchantId 商户唯一标识
     * @return 商户店铺列表，无数据返回空集合
     */
    @Override
    @Transactional(readOnly = true)
    public List<Store> getStoresByMerchant(Long merchantId) {
        // Step1: 参数有效性检查
        if (merchantId == null) {
            System.out.println("[WARN] 查询失败：商家ID为空");
            return List.of();
        }

        // Step2: 构建查询条件
        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Store::getMerchantId, merchantId);

        // Step3: 执行查询
        return storeMapper.selectList(queryWrapper);
    }

    /* ========================= 店铺更新 ========================= */

    /**
     * 更新店铺信息
     * @param store 包含更新字段的店铺对象（必须包含有效ID）
     * @return 更新成功返回true，否则false
     */
    @Override
    @Transactional
    public boolean updateStore(Store store) {
        // Step1: 基础参数校验
        if (!validateUpdateParams(store)) {
            System.out.println("[WARN] 更新失败：店铺ID为空");
            return false;
        }

        // Step2: 获取持久化对象
        Store existing = getExistingStore(store.getId());
        if (existing == null) return false;

        // Step3: 名称变更校验
        if (needCheckNameUpdate(store, existing) && isStoreNameExists(store.getName())) {
            System.out.println("[WARN] 更新失败：新名称 '" + store.getName() + "' 已存在");
            return false;
        }

        // Step4:

        // Step4: 执行更新
        return storeMapper.updateById(store) > 0;
    }

    /**
     * 切换店铺营业状态
     * @param storeId 店铺唯一标识
     * @param status 目标状态（0-关闭，1-营业）
     * @return 修改成功返回true，否则false
     */
    @Override
    @Transactional
    public boolean toggleStoreStatus(Long storeId, int status) {
        // Step1: 参数校验
        if (!validateStatusParams(storeId, status)) return false;

        // Step2: 构建更新对象
        Store update = new Store();
        update.setId(storeId);
        update.setStatus(status);

        // Step3: 执行更新
        return storeMapper.updateById(update) > 0;
    }

    /* ========================= 店铺删除 ========================= */

    /**
     * 删除指定店铺
     * @param storeId 店铺唯一标识
     * @return 删除成功返回true，否则false
     */
    @Override
    @Transactional
    public boolean deleteStore(Long storeId) {
        // Step1: 存在性校验
        if (!isStoreExists(storeId)) {
            System.out.println("[WARN] 删除失败：店铺不存在 ID=" + storeId);
            return false;
        }

        // Step2: 执行删除
        return storeMapper.deleteById(storeId) > 0;
    }

    /* ========================= 业务校验 ========================= */

    /**
     * 验证商户对店铺的所有权
     * @param storeId 店铺唯一标识
     * @param merchantId 商户唯一标识
     * @return 所有权有效返回true，否则false
     */
    @Override
    @Transactional(readOnly = true)
    public boolean validateStoreOwnership(Long storeId, Long merchantId) {
        // Step1: 参数基础校验
        if (storeId == null || merchantId == null) {
            System.out.println("[ERROR] 权限校验失败：参数为空");
            return false;
        }

        // Step2: 获取店铺信息
        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            System.out.println("[ERROR] 权限校验失败：店铺不存在 ID=" + storeId);
            return false;
        }

        // Step3: 所有权校验
        return store.getMerchantId().equals(merchantId);
    }

    /* ------------------------- 私有方法 ------------------------- */

    /**
     * 校验创建参数有效性
     * @param store 待校验的店铺对象
     * @return 参数有效返回true，否则false
     */
    private boolean validateCreateParams(Store store) {
        return store.getMerchantId() != null &&
                StringUtils.hasText(store.getName());
    }

    /**
     * 校验更新参数有效性
     * @param store 待校验的店铺对象
     * @return 参数有效返回true，否则false
     */
    private boolean validateUpdateParams(Store store) {
        return store.getId() != null;
    }

    /**
     * 校验状态参数有效性
     * @param storeId 店铺ID
     * @param status 目标状态
     * @return 参数有效返回true，否则false
     */
    private boolean validateStatusParams(Long storeId, int status) {
        if (storeId == null || (status != 0 && status != 1)) {
            System.out.println("[WARN] 状态修改失败：参数不合法 status=" + status);
            return false;
        }
        return isStoreExists(storeId);
    }

    /**
     * 检查店铺名称是否存在
     * @param name 待检查的店铺名称
     * @return 存在返回true，否则false
     */
    private boolean isStoreNameExists(String name) {
        return StringUtils.hasText(name) &&
                storeMapper.selectCount(new LambdaQueryWrapper<Store>()
                        .eq(Store::getName, name)) > 0;
    }

    /**
     * 检查店铺是否存在
     * @param storeId 店铺唯一标识
     * @return 存在返回true，否则false
     */
    private boolean isStoreExists(Long storeId) {
        return storeId != null &&
                storeMapper.selectById(storeId) != null;
    }

    /**
     * 获取已存在的店铺对象
     * @param storeId 店铺唯一标识
     * @return 存在返回店铺对象，否则null
     */
    private Store getExistingStore(Long storeId) {
        Store existing = storeMapper.selectById(storeId);
        if (existing == null) {
            System.out.println("[WARN] 操作失败：店铺不存在 ID=" + storeId);
        }
        return existing;
    }

    /**
     * 判断是否需要检查名称变更
     * @param newData 新数据对象
     * @param existing 已存在的数据对象
     * @return 需要检查返回true，否则false
     */
    private boolean needCheckNameUpdate(Store newData, Store existing) {
        return !newData.getName().equals(existing.getName());
    }
}