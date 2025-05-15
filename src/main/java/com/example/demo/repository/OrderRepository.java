package com.example.demo.repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.Shop;

@Repository
public class OrderRepository {

    // 模拟数据存储
    private final List<Order> mockOrders = new ArrayList<>();
    private final Random random = new Random();

    // 初始化时生成测试数据
    public OrderRepository() {
        generateMockOrders(50); // 默认生成50个测试订单
    }

    public List<Order> findByStoreIdAndStatusAndCreatedAtBetween(
            Long storeId,
            Order.OrderStatus status,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return mockOrders.stream()
                .filter(order
                        -> storeId.equals(order.getStoreId())
                && status == order.getStatus()
                && !order.getCreatedAt().isBefore(start)
                && !order.getCreatedAt().isAfter(end)
                )
                .collect(Collectors.toList());
    }

    /**
     * 根据店铺ID查询订单（模拟数据库查询）
     *
     * @param storeId 店铺ID
     * @return 匹配的订单列表（可能为空）
     */
    public List<Order> findByStoreId(Long storeId) {
        if (storeId == null) {
            return Collections.emptyList();
        }

        return mockOrders.stream()
                .filter(order -> storeId.equals(order.getStoreId()))
                .collect(Collectors.toList());
    }

    /**
     * 生成模拟订单数据
     *
     * @param count 生成数量
     */
    private void generateMockOrders(int count) {
        for (int i = 0; i < count; i++) {
            Order order = new Order();
            order.setId((long) i);
            order.setUserId(2000L + random.nextInt(50)); // 用户ID范围：2000-2049
            order.setStoreId(Shop.sampleStoreIds[random.nextInt(Shop.sampleStoreIds.length)]); // 随机分配店铺

            // 30%概率分配骑手
            if (random.nextDouble() < 0.3) {
                order.setRiderId(3000L + random.nextInt(20));
            }

            // 状态及时间逻辑
            Order.OrderStatus status = generateRandomStatus();
            order.setStatus(status);
            order.setTotalPrice(generateRandomAmount());
            order.setCreatedAt(generateRandomPastDate(30)); // 过去30天内创建

            // 截止时间：创建时间 + 1-48小时
            order.setDeadline(order.getCreatedAt().plusHours(1 + random.nextInt(48)));

            // 结束时间：仅完成/取消状态有值
            if (status == Order.OrderStatus.COMPLETED || status == Order.OrderStatus.CANCELLED) {
                order.setEndedAt(generateRandomEndDate(order.getCreatedAt()));
            }

            mockOrders.add(order);
        }
    }

    /**
     * 生成随机状态（权重分配）
     */
    private Order.OrderStatus generateRandomStatus() {
        double prob = random.nextDouble();
        if (prob < 0.4) {
            return Order.OrderStatus.COMPLETED;       // 40% 已完成

                }if (prob < 0.6) {
            return Order.OrderStatus.PREPARING;       // 20% 准备中

                }if (prob < 0.8) {
            return Order.OrderStatus.DELIVERING;      // 20% 配送中

                }if (prob < 0.95) {
            return Order.OrderStatus.PENDING;        // 15% 等待处理

                }return Order.OrderStatus.CANCELLED;                       // 5% 已取消
    }

    /**
     * 生成随机金额（10.00 - 999.99）
     */
    private BigDecimal generateRandomAmount() {
        return BigDecimal.valueOf(10 + random.nextDouble() * 990)
                .setScale(2, RoundingMode.HALF_UP); // 使用枚举替代int常量
    }

    /**
     * 生成过去N天的随机时间
     */
    private LocalDateTime generateRandomPastDate(int maxDaysAgo) {
        long minDay = LocalDateTime.now().minusDays(maxDaysAgo).toEpochSecond(ZoneOffset.UTC);
        long maxDay = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDateTime.ofEpochSecond(randomDay, 0, ZoneOffset.UTC);
    }

    /**
     * 生成订单结束时间（基于创建时间）
     */
    private LocalDateTime generateRandomEndDate(LocalDateTime createdAt) {
        return createdAt.plusHours(1 + random.nextInt(72)) // 1-72小时后结束
                .plusMinutes(random.nextInt(60));
    }
}
