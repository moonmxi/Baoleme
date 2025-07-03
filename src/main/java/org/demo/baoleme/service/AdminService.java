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

    List<User> getAllUsersPaged(int page, int pageSize, String keyword, String gender, Long startId, Long endId);

    /**
     * 分页查询所有骑手
     */
    List<Rider> getAllRidersPaged(int page, int pageSize, String keyword, Long  startId, Long endId, Integer status, Integer  dispatchMode, Long startBalance, Long endBalance);

    List<Merchant> getAllMerchantsPaged(int page, int pageSize, String keyword, Long startId, Long endId);

    List<Store> getAllStoresPaged(int page, int pageSize, String keyword, String type, Integer status, BigDecimal startRating, BigDecimal endRating);

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

    Order getOrderById(Long orderId);

    Review getReviewById(Long reviewId);

}