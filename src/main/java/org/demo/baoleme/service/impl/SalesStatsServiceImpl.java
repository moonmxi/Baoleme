package org.demo.baoleme.service.impl;

import org.demo.baoleme.dto.request.salesStats.SaleTrendStatsRequest;
import org.demo.baoleme.dto.response.salesStats.SaleTrendData;
import org.demo.baoleme.mapper.ProductMapper;
import org.demo.baoleme.mapper.SaleMapper;
import org.demo.baoleme.pojo.Product;
import org.demo.baoleme.pojo.ProductSalesDTO;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.service.SalesStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SalesStatsServiceImpl implements SalesStatsService {

    private final SaleMapper saleMapper;
    private final ProductMapper productMapper;

    public SalesStatsServiceImpl(SaleMapper saleMapper, ProductMapper productMapper) {
        this.productMapper = productMapper;
        this.saleMapper = saleMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalSales(Long storeId, LocalDate startDate, LocalDate endDate) {
        return saleMapper.sumTotalAmountByStoreAndDate(
                storeId,
                startDate,
                endDate
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleTrendData> getSalesTrend(
            Long storeId,
            SaleTrendStatsRequest.TimeAxis timeAxis,
            int days
    ) {
        // Step 1: 将TimeAxis枚举转换为SQL日期格式
        String dateFormat = resolveDateFormat(timeAxis);

        // Step 2: 计算日期范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        // Step 3: 调用Mapper方法
        return saleMapper.findSalesTrend(
                storeId,
                dateFormat,  // 传入格式化字符串
                startDate,
                endDate
        );
    }

    // 辅助方法：根据时间轴类型解析日期格式
    private String resolveDateFormat(SaleTrendStatsRequest.TimeAxis timeAxis) {
        return switch (timeAxis) {
            case BY_DAY -> "%Y-%m-%d";  // 示例：2023-10-01
            case BY_WEEK -> "%Y-%u";    // 示例：2023-40（第40周）
            case BY_MONTH -> "%Y-%m";   // 示例：2023-10
        };
    }

    @Override
    @Transactional(readOnly = true)
    public int getOrderCount(Long storeId, LocalDate startDate, LocalDate endDate) {
        // 假设 SaleMapper 中有获取订单数量的方法
        return saleMapper.getOrderCount(storeId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getPopularProducts(Long storeId, LocalDate startDate, LocalDate endDate) {
        // Step2: 获取销量数据
        List<ProductSalesDTO> salesData = saleMapper.selectTop3ProductsByStore(storeId);
        if (salesData.isEmpty()) {
            System.out.println("店铺: " + storeId + "  没找到销售数据");
            return List.of();
        }
        else {
            System.out.println("销售数据" + salesData);
        }

        // Step3: 提取商品ID集合
        List<Long> productIds = salesData.stream()
                .map(ProductSalesDTO::getProductId)
                .collect(Collectors.toList());

        // Step4: 批量查询商品详情
        return productMapper.selectBatchIds(productIds).stream()
                .sorted(Comparator.comparingInt(p ->
                        -salesData.stream()
                                .filter(d -> d.getProductId().equals(p.getId()))
                                .findFirst()
                                .map(ProductSalesDTO::getTotalQuantity)
                                .orElse(0)
                )) // 按销量降序排序
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BigDecimal getWeightedAveragePriceOfPopularProducts(Long storeId) {
        List<ProductSalesDTO> salesData = saleMapper.selectTop3ProductsByStore(storeId);

        if (salesData.isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<Long> productIds = salesData.stream()
                .map(ProductSalesDTO::getProductId)
                .collect(Collectors.toList());

        Map<Long, Product> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        BigDecimal totalValue = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (ProductSalesDTO sale : salesData) {
            Product product = productMap.get(sale.getProductId());
            if (product != null && product.getPrice() != null) {
                totalValue = totalValue.add(product.getPrice().multiply(new BigDecimal(sale.getTotalQuantity())));
                totalQuantity += sale.getTotalQuantity();
            }
        }

        return totalQuantity > 0
                ? totalValue.divide(new BigDecimal(totalQuantity), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public int getProductVolume(Long productId){
        // Step2: 检查id是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            System.out.println("错误：商品ID不存在");
            return -1;
        }

        return productMapper.getProductVolume(productId);
    }
}