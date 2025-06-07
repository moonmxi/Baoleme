package org.demo.baoleme.service.impl;

import org.demo.baoleme.common.RedisLockUtil;
import org.demo.baoleme.dto.request.order.CartItemDTO;
import org.demo.baoleme.dto.request.order.OrderCreateRequest;
import org.demo.baoleme.dto.response.user.UserCreateOrderResponse;
import org.demo.baoleme.dto.response.user.UserSearchOrderItemResponse;
import org.demo.baoleme.mapper.*;
import org.demo.baoleme.pojo.*;
import org.demo.baoleme.service.CartService;
import org.demo.baoleme.service.OrderService;
import org.demo.baoleme.service.StoreService;
import org.demo.baoleme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisLockUtil redisLockUtil;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private RiderMapper riderMapper;

    @Override
    public List<Order> getAvailableOrders(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.selectAvailableOrders(offset, pageSize);
    }

    @Override
    public boolean grabOrder(Long orderId, Long riderId) {
        String lockKey = "order_lock:" + orderId;
        String lockValue = String.valueOf(riderId);
        boolean locked = redisLockUtil.tryLock(lockKey, lockValue, 30, TimeUnit.SECONDS);
        if (!locked) {
            return false;  // 没抢到锁，直接返回
        }
        try {
            // 加锁后执行数据库更新
            return orderMapper.grabOrder(orderId, riderId) > 0;
        } finally {
            redisLockUtil.unlock(lockKey, lockValue);
        }
    }

    @Override
    public boolean riderCancelOrder(Long orderId, Long riderId) {
        return orderMapper.riderCancelOrder(orderId, riderId) > 0;
    }

    @Override
    public boolean riderUpdateOrderStatus(Long orderId, Long riderId, Integer targetStatus) {
        if (targetStatus != null && targetStatus == 3) {

            // 特判：完成订单，调用专门 SQL
            return (orderMapper.completeOrder(orderId, riderId) > 0 && riderMapper.updateRiderOrderStatusAfterOrderCompletion(riderId) > 0);
        } else {
            // 其他普通状态
            //System.out.println("1");
            return orderMapper.riderUpdateOrderStatus(orderId, riderId, targetStatus) > 0;
        }
    }

    @Override
    public List<Order> getRiderOrders(Long riderId, Integer status, LocalDateTime startTime, LocalDateTime endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.selectRiderOrders(riderId, status, startTime, endTime, offset, pageSize);
    }

    @Override
    public Map<String, Object> getRiderEarnings(Long riderId) {
        return orderMapper.selectRiderEarnings(riderId);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderMapper.selectById(orderId);
    }


    private BigDecimal applyCouponDiscount(BigDecimal totalAmount, Coupon coupon) {
        if (coupon.getType() == 1) { // 折扣券
            return totalAmount.multiply(coupon.getDiscount());
        } else if (coupon.getType() == 2) { // 满减券
            if (totalAmount.compareTo(coupon.getFullAmount()) >= 0) {
                return totalAmount.subtract(coupon.getReduceAmount());
            }
        }
        return totalAmount;
    }

    private String generateOrderNumber() {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    @Override
    public List<Order> getOrderByStoreId(Long storeId) {
        return orderMapper.selectByStoreId(storeId);
    }

    @Override
    public List<Order> getOrdersByMerchant(Long storeId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.selectByStoreIdUsingPage(storeId, offset, pageSize, null);
    }

    @Override
    public List<Order> getOrdersByMerchantAndStatus(Long storeId, Integer status, int page, int pageSize){
        int offset = (page - 1) * pageSize;
        return orderMapper.selectByStoreIdUsingPage(storeId, offset, pageSize, status);
    }

    @Override
    public boolean updateOrderByMerchant(Long merchantId, Long orderId, Integer newStatus) {
        // Step 1: 查询订单当前状态和店铺ID
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return false; // 订单不存在
        }

        if(!storeService.validateStoreOwnership(order.getStoreId(), merchantId)){
            return false; // 商家无权处理订单
        }

        // Step 2: 执行更新操作
        int rowsUpdated = orderMapper.updateByMerchant(
                orderId,
                newStatus
        );

        // Step 3: 返回更新结果
        return rowsUpdated > 0;
    }

    @Override
    @Transactional
    public UserCreateOrderResponse createOrder(Long userId, OrderCreateRequest request) {
        // 1. 基础参数校验
        if (userId == null || request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("参数不能为空或商品列表为空");
        }

        // 2. 查询用户，获取用户默认地址（示例）
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 这里假设你有方法获取用户地址
        String userLocation = request.getUserLocation();

        // 3. 查询店铺，获取店铺地址
        Store store = storeMapper.selectById(request.getStoreId());
        if (store == null) {
            throw new RuntimeException("店铺不存在");
        }
        String storeLocation = store.getLocation();

        // 4. 商品库存和价格验证，并计算商品总价
        BigDecimal totalProductPrice = BigDecimal.ZERO;
        for (CartItemDTO item : request.getItems()) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在，ID: " + item.getProductId());
            }
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品库存不足：" + product.getName());
            }
            totalProductPrice = totalProductPrice.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // 5. 优惠券处理，先计算优惠后的商品总价
        BigDecimal discountedPrice = totalProductPrice;
        Coupon coupon = null;
        if (request.getCouponId() != null) {
            coupon = couponMapper.selectById(request.getCouponId());
            if (coupon == null) {
                throw new RuntimeException("优惠券不存在");
            }
            // TODO: 添加优惠券归属和有效期校验
            if (coupon.getType() == 1) {
                discountedPrice = totalProductPrice.multiply(coupon.getDiscount());
            } else if (coupon.getType() == 2 && totalProductPrice.compareTo(coupon.getFullAmount()) >= 0) {
                discountedPrice = totalProductPrice.subtract(coupon.getReduceAmount());
            }
        }

        // 6. 加上配送费，计算最终支付金额
        BigDecimal deliveryPrice = request.getDeliveryPrice() != null ? request.getDeliveryPrice() : BigDecimal.ZERO;
        BigDecimal actualPrice = discountedPrice.add(deliveryPrice);


        // 7. 创建订单实体并插入数据库
        Order order = new Order();
        order.setUserId(userId);
        order.setStoreId(request.getStoreId());
        order.setUserLocation(userLocation);
        order.setStoreLocation(storeLocation);
        order.setStatus(0);  // 待接单状态
        order.setTotalPrice(totalProductPrice);   // 商品原价总和，不含优惠和配送费
        order.setActualPrice(actualPrice);         // 实际支付金额（优惠后 + 配送费）
        order.setCreatedAt(LocalDateTime.now());
        order.setDeadline(request.getDeadline()); // 订单截止时间30分钟后
        order.setRemark(request.getRemark());
        order.setDeliveryPrice(deliveryPrice);
        orderMapper.insert(order);

        // 8. 创建订单项并扣库存
        for (CartItemDTO item : request.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItemMapper.insert(orderItem);

            productMapper.decreaseStock(item.getProductId(), item.getQuantity());
        }

        // 9. 处理优惠券状态，标记为已使用
        if (coupon != null) {
            couponMapper.markAsUsed(coupon.getId());
        }

        // 10. 清空购物车
        cartService.removeCart(userId);

        // 11. 返回订单信息
        UserCreateOrderResponse response = new UserCreateOrderResponse();
        response.setOrderId(order.getId());
        response.setTotalPrice(totalProductPrice);
        response.setActualPrice(actualPrice);
        response.setStatus(order.getStatus());
        response.setStoreId(order.getStoreId());
        response.setStoreName(store.getName());
        response.setRemark(order.getRemark());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(orderMapper.selectOrderItemsWithProductInfo(order.getId()));
        return response;
    }
    @Override
    @Transactional
    public List<UserSearchOrderItemResponse> getOrderItemById(Long orderId){
        List<UserSearchOrderItemResponse> result = productMapper.selectByOrderId(orderId);

        return result;
    }
}