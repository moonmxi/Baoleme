package org.demo.baoleme.service;

import org.demo.baoleme.dto.request.salesStats.SaleTrendStatsRequest;
import org.demo.baoleme.dto.response.salesStats.SaleTrendData;
import org.demo.baoleme.pojo.Product;
import org.demo.baoleme.pojo.Sale;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SalesStatsService {

    BigDecimal getTotalSales(Long storeId, LocalDate startDate, LocalDate endDate);

    List<SaleTrendData> getSalesTrend(Long storeId, SaleTrendStatsRequest.TimeAxis timeAxis, int days);

    int getOrderCount(Long storeId, LocalDate startDate, LocalDate endDate);

    List<Product> getPopularProducts(Long storeId, LocalDate startDate, LocalDate endDate);

    BigDecimal getWeightedAveragePriceOfPopularProducts(Long storeId);

    int getProductVolume(Long productId);
}