// 文件位置：src/main/java/com/example/demo/service/OrderStatsService.java
package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Order;
import com.example.demo.repository.OrderRepository;

@Service
public class OrderStatsService {

    private final OrderRepository orderRepository;

    public OrderStatsService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * 获取指定时间范围内的完成订单统计
     *
     * @param storeId 店铺ID
     * @param start 开始时间
     * @param end 结束时间
     * @return 包含统计数据的Map
     */
    public Map<String, Object> getCompletedOrderStats(Long storeId, LocalDateTime start, LocalDateTime end) {
        // 1. 查询符合条件的已完成订单
        List<Order> completedOrders = orderRepository.findByStoreIdAndStatusAndCreatedAtBetween(
                storeId,
                Order.OrderStatus.COMPLETED,
                start,
                end
        );

        // 2. 初始化默认统计结果
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int orderCount = 0;
        BigDecimal averageDaily = BigDecimal.ZERO;
        LocalDate bestDayDate = null;
        BigDecimal bestDayAmount = BigDecimal.ZERO;
        Map<LocalDate, BigDecimal> dailySales = Collections.emptyMap();

        Map<LocalDate, Map<String, Object>> dailyStats = new LinkedHashMap<>();

        if (!completedOrders.isEmpty()) {
            // 3. 按日期分组订单
            Map<LocalDate, List<Order>> dailyOrders = completedOrders.stream()
                    .collect(Collectors.groupingBy(
                            order -> order.getCreatedAt().toLocalDate(),
                            LinkedHashMap::new, // 保持日期顺序
                            Collectors.toList()
                    ));

            // 4. 计算每日统计数据
            // 表格模式
            dailyOrders.forEach((date, orders) -> {
                int dayOrderCount = orders.size();
                BigDecimal dayTotal = orders.stream()
                        .map(Order::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal dayAvg = dayTotal.divide(
                        BigDecimal.valueOf(dayOrderCount), 2, RoundingMode.HALF_UP
                );

                Map<String, Object> dayStats = new HashMap<>();
                dayStats.put("count", dayOrderCount);
                dayStats.put("total", dayTotal);
                dayStats.put("avg", dayAvg);
                dailyStats.put(date, dayStats);
            });

            // 3. 计算总营业额和订单数量
            totalRevenue = completedOrders.stream()
                    .map(Order::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            orderCount = completedOrders.size();

            // 4. 计算日均营业额（避免除零错误）
            if (orderCount > 0) {
                averageDaily = totalRevenue.divide(
                        BigDecimal.valueOf(orderCount),
                        2,
                        RoundingMode.HALF_UP
                );
            }

            // 5. 按日期分组计算每日销售额
            dailySales = completedOrders.stream()
                    .collect(Collectors.groupingBy(
                            order -> order.getCreatedAt().toLocalDate(),
                            Collectors.reducing(
                                    BigDecimal.ZERO,
                                    Order::getTotalPrice,
                                    BigDecimal::add
                            )
                    ));

            // 6. 查找最佳销售日
            Optional<Map.Entry<LocalDate, BigDecimal>> bestDayOpt = dailySales.entrySet().stream()
                    .max(Map.Entry.comparingByValue());

            if (bestDayOpt.isPresent()) {
                Map.Entry<LocalDate, BigDecimal> bestDay = bestDayOpt.get();
                bestDayDate = bestDay.getKey();
                bestDayAmount = bestDay.getValue();
            }
        }

        // 图表模式
        // 预处理标签和金额
        List<String> labels = dailySales.keySet().stream()
                .sorted() // 按日期排序
                .map(date -> date.format(DateTimeFormatter.ofPattern("MM-dd")))
                .collect(Collectors.toList());

        List<Double> amounts = dailySales.values().stream()
                .map(BigDecimal::doubleValue)
                .collect(Collectors.toList());

        // 7. 封装结果
        return Map.of(
                "totalRevenue", totalRevenue,
                "orderCount", orderCount,
                "averageDaily", averageDaily,
                "bestDayDate", bestDayDate != null ? bestDayDate.toString() : "N/A",
                "bestDayAmount", bestDayAmount,
                "dailySales", dailySales,
                "dailyStats", dailyStats,
                "chartLabels", labels,
                "chartAmounts", amounts
        );
    }
}
