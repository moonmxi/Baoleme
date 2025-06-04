package org.demo.baoleme.service.impl;

import org.demo.baoleme.mapper.*;
import org.demo.baoleme.pojo.*;
import org.demo.baoleme.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RiderMapper riderMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public Admin login(Long id, String password) {
        Admin admin = adminMapper.selectById(id);
        if (admin != null && password.equals(admin.getPassword())) {
            return admin;
        }
        return null;
    }

    @Override
    public List<User> getAllUsersPaged(int page, int pageSize,String keyword ,String gender, Long startId, Long endId) {
        int offset = (page - 1) * pageSize;
        return userMapper.selectUsersPaged(keyword, gender, startId, endId, offset, pageSize);
    }

    @Override
    public List<Rider> getAllRidersPaged(int page, int pageSize,String keyword, Long startId, Long endId, Integer status, Integer dispatchMode, Long startBalance, Long endBalance) {
        int offset = (page - 1) * pageSize;
        return riderMapper.selectRidersPaged(offset, pageSize, keyword, startId, endId, status, dispatchMode, startBalance, endBalance);
    }

    @Override
    public List<Merchant> getAllMerchantsPaged(int page, int pageSize, String keyword, Long startId, Long endId) {
        int offset = (page - 1) * pageSize;
        return merchantMapper.selectMerchantsPaged(offset, pageSize, keyword, startId, endId);
    }

    @Override
    public List<Store> getAllStoresPaged(int page, int pageSize, String keyword, String type, Integer status, BigDecimal startRating, BigDecimal endRating) {
        int offset = (page - 1) * pageSize;
        return storeMapper.selectStoresPaged(offset, pageSize, keyword, type, status, startRating, endRating);
    }

    @Override
    public boolean deleteUserByUsername(String username) {
        return userMapper.deleteByUsername(username) > 0;
    }

    @Override
    public boolean deleteRiderByUsername(String username) {
        return riderMapper.deleteByUsername(username) > 0;
    }

    @Override
    public boolean deleteMerchantByUsername(String username) {
        return merchantMapper.deleteByUsername(username) > 0;
    }

    @Override
    public boolean deleteStoreByName(String storeName) {
        return storeMapper.deleteByName(storeName) > 0;
    }

    @Override
    public boolean deleteProductByNameAndStore(String productName, String storeName) {
        return productMapper.deleteByNameAndStore(productName, storeName) > 0;
    }

    @Override
    public List<Order> getAllOrdersPaged(Long userId,
                                         Long storeId,
                                         Long riderId,
                                         Integer status,
                                         LocalDateTime createdAt,
                                         LocalDateTime endedAt,
                                         int page,
                                         int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.selectOrdersPaged(userId, storeId, riderId, status, createdAt, endedAt, offset, pageSize);
    }

    @Override
    public List<Review> getReviewsByCondition(Long userId, Long storeId, Long productId,
                                              LocalDateTime startTime, LocalDateTime endTime,
                                              int page, int pageSize, BigDecimal startRating, BigDecimal endRating) {
        int offset = (page - 1) * pageSize;
        return reviewMapper.selectReviewsByCondition(userId, storeId, productId, startTime, endTime, offset, pageSize, startRating, endRating);
    }

    @Override
    public List<Map<String, Object>> searchStoreAndProductByKeyword(String keyword) {
        List<Map<String, Object>> stores = storeMapper.searchStoresByKeyword(keyword);
        List<Map<String, Object>> products = storeMapper.searchProductsByKeyword(keyword);

        // 使用 Map<Long, Map<String, Object>> 汇总数据（Long 为 store_id）
        Map<Long, Map<String, Object>> resultMap = new LinkedHashMap<>();

        // 先填入店铺
        for (Map<String, Object> store : stores) {
            Object idObj = store.get("id");
            Object nameObj = store.get("name");

            Long storeId = (idObj instanceof Number) ? ((Number) idObj).longValue() : null;
            String storeName = (nameObj instanceof String) ? (String) nameObj : "";

            if (storeId == null) {
                // 可选：跳过无效数据
                continue;
            }

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("store_id", storeId);
            entry.put("store_name", storeName);
            entry.put("products", new LinkedHashMap<String, Long>());  // 初始化空的产品 map

            resultMap.put(storeId, entry);
        }

        // 再填入商品
        for (Map<String, Object> product : products) {
            Long storeId = ((Number) product.get("store_id")).longValue();
            String storeName = (String) product.get("store_name");
            String productName = (String) product.get("product_name");
            Long productId = ((Number) product.get("product_id")).longValue();

            // 若该店铺之前没搜到，先初始化
            if (!resultMap.containsKey(storeId)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("store_id", storeId);
                entry.put("store_name", storeName);
                entry.put("products", new LinkedHashMap<String, Long>());
                resultMap.put(storeId, entry);
            }

            // 添加商品信息
            Map<String, Long> productMap = (Map<String, Long>) resultMap.get(storeId).get("products");
            productMap.put(productName, productId);
        }

        return new ArrayList<>(resultMap.values());
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderMapper.selectById(orderId);
    }

    @Override
    public Review getReviewById(Long reviewId) {
        return reviewMapper.selectById(reviewId);
    }
}