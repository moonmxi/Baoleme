package org.demo.baoleme.service.impl;

import org.demo.baoleme.common.RedisLockUtil;
import org.demo.baoleme.dto.request.user.UserCreateOrderRequest;
import org.demo.baoleme.dto.response.user.UserCreateOrderResponse;
import org.demo.baoleme.mapper.*;
import org.demo.baoleme.pojo.*;
import org.demo.baoleme.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisLockUtil redisLockUtil;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CouponMapper couponMapper;

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
            return orderMapper.completeOrder(orderId, riderId) > 0;
        } else {
            // 其他普通状态
            System.out.println("1");
            return orderMapper.riderUpdateOrderStatus(orderId, riderId, targetStatus) > 0;
        }
    }

    @Override
    public List<Order> getRiderOrders(Long riderId, Integer status, String startTime, String endTime, int page, int pageSize) {
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

    @Override
    @Transactional
    public UserCreateOrderResponse createOrder(Long userId, UserCreateOrderRequest request) {
        UserCreateOrderResponse response = new UserCreateOrderResponse();
        // 1. 参数校验
        if (userId == null || request == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 2. 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 验证店铺是否存在
        if (!userMapper.existsShop(request.getStoreId())) {
            throw new RuntimeException("店铺不存在");
        }

        // 4. 验证商品和库存
        for (OrderItem item : request.getItems()) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在: " + item.getProductId());
            }
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品库存不足: " + product.getName());
            }
            if (item.getPrice().compareTo(product.getPrice()) != 0) {
                throw new RuntimeException("商品价格已变更: " + product.getName());
            }
        }

        // 5. 验证优惠券
        Coupon coupon = null;
        if (request.getCouponId() != null) {
            coupon = couponMapper.selectById(request.getCouponId());
            if (coupon == null || !coupon.getUserId().equals(userId)) {
                throw new RuntimeException("优惠券不可用");
            }
            if (coupon.getExpirationDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("优惠券已过期");
            }
        }

        // 6. 计算总金额
        BigDecimal totalAmount = calculateTotalAmount(request.getItems());
        BigDecimal actualAmount = null;
        //配送费未定

        // 应用优惠券折扣
        if (coupon != null) {
            if(coupon.getType()==1){
                actualAmount = applyCouponDiscount(totalAmount, coupon);
            }else if (coupon.getType()==2){
                BigDecimal reduceAmount = coupon.getReduceAmount();
                actualAmount = totalAmount.subtract(reduceAmount);
            }
        }

        // 7. 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setStoreId(request.getStoreId());
        order.setTotalPrice(totalAmount);
        order.setActualPrice(actualAmount);
        order.setStatus(0); // 0-待支付
        order.setRemark(request.getRemark());
        order.setAddress(request.getAddress());
        order.setCreatedAt(LocalDateTime.now());

        orderMapper.insert(order);

        // 8. 创建订单项并扣减库存
        for (OrderItem itemRequest : request.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(itemRequest.getProductId());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(itemRequest.getPrice());
            orderItemMapper.insert(orderItem);

            // 扣减库存
            productMapper.decreaseStock(itemRequest.getProductId(), itemRequest.getQuantity());
        }

        // 9. 标记优惠券为已使用(如果使用了优惠券)
        if (coupon != null) {
            couponMapper.markAsUsed(coupon.getId());
        }

        return response;
    }


    private BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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

//    private String generateOrderNumber() {
//        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
//                UUID.randomUUID().toString().substring(0, 6).toUpperCase();
//    }
}
