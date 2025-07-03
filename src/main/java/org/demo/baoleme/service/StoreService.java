package org.demo.baoleme.service;

import org.demo.baoleme.pojo.Store;

import java.util.List;

public interface StoreService {

    /**
     * 创建店铺
     * @param store 店铺信息（无需传入ID）
     * @return 包含生成的ID的店铺对象，失败返回null
     */
    Store createStore(Store store);

    /**
     * 根据ID获取店铺详情
     * @param storeId 店铺ID
     * @return 店铺对象或null
     */
    Store getStoreById(Long storeId);

    /**
     * 查询商家所有店铺
     * @param merchantId 商家ID
     * @return 店铺列表（可能为空）
     */
    List<Store> getStoresByMerchant(Long merchantId, int currentPage, int pageSize);

    /**
     * 更新店铺信息
     * @param store 需包含ID的完整店铺对象
     * @return 是否成功
     */
    boolean updateStore(Store store);

    /**
     * 切换店铺状态
     * @param storeId 店铺ID
     * @param newStatus 新状态（ENABLED/DISABLED）
     * @return 是否成功
     */
    boolean toggleStoreStatus(Long storeId, int newStatus);

    /**
     * 删除店铺
     * @param storeId 店铺ID
     * @return 是否成功
     */
    boolean deleteStore(Long storeId);

    /**
     * 验证所有关系
     * @param storeId 店铺ID
     * @param merchantId 商家ID
     * @return true if the store belongs to the merchant
     */
    boolean validateStoreOwnership(Long storeId, Long merchantId);

    boolean updateImage(Long storeId, String imagePath);
}