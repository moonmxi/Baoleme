package org.demo.baoleme.service.impl;

import org.demo.baoleme.dto.request.salesStats.SaleTrendStatsRequest;
import org.demo.baoleme.dto.response.salesStats.SaleTrendData;
import org.demo.baoleme.mapper.ProductMapper;
import org.demo.baoleme.mapper.SaleMapper;
import org.demo.baoleme.pojo.Product;
import org.demo.baoleme.service.SalesStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
        // TODO: 由于未定义 popularProduct，这里返回所有商品
        return productMapper.selectByStoreId(storeId);
    }
}