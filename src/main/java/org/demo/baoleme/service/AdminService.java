package org.demo.baoleme.service;

import org.demo.baoleme.pojo.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AdminService {

    /**
     * 登录验证管理员账号密码
     */
    Admin login(Long id, String password);

    /**
     * 查询管理员信息（可用于 /info 接口）
     */
    Admin getInfo(Long id);

    List<User> getAllUsersPaged(int page, int pageSize);

    /**
     * 分页查询所有骑手
     */
    List<Rider> getAllRidersPaged(int page, int pageSize);

    List<Merchant> getAllMerchantsPaged(int page, int pageSize);

    List<Store> getAllStoresPaged(int page, int pageSize);

    boolean deleteUserByUsername(String username);

    boolean deleteRiderByUsername(String username);

    boolean deleteMerchantByUsername(String username);

    boolean deleteStoreByName(String storeName);

    boolean deleteProductByNameAndStore(String productName, String storeName);

    List<Order> getAllOrdersPaged(Long userId,
                                  Long storeId,
                                  Long riderId,
                                  Integer status,
                                  LocalDateTime createdAt,
                                  LocalDateTime endedAt,
                                  int page,
                                  int pageSize);

    /**
     * 按条件分页查询评价
     */
    List<Review> getReviewsByCondition(Long userId, Long storeId, Long productId,
                                       LocalDateTime startTime, LocalDateTime endTime,
                                       int page, int pageSize, BigDecimal startSating, BigDecimal endSating);

    /**
     * 根据关键词搜索店铺与商品信息
     */
    List<Map<String, Object>> searchStoreAndProductByKeyword(String keyword);


}