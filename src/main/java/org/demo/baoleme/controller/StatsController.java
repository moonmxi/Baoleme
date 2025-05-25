package org.demo.baoleme.controller;

import jakarta.validation.Valid;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.salesStats.*;
import org.demo.baoleme.dto.response.salesStats.*;
import org.demo.baoleme.pojo.Product;
import org.demo.baoleme.service.SalesStatsService;
import org.demo.baoleme.service.StoreService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stats-store")
public class StatsController {
    private final SalesStatsService salesStatsService;
    private final StoreService storeService;

    public StatsController(SalesStatsService salesStatsService, StoreService storeService) {
        this.salesStatsService = salesStatsService;
        this.storeService = storeService;
    }

    @PostMapping("/overview")
    public CommonResponse getSalesOverview(
            @RequestHeader("Authorization") String tokenHeader,
            @Valid @RequestBody SaleOverviewStatsRequest request
    ) {
        System.out.println("收到请求：" + request);

        if(!storeService.validateStoreOwnership(request.getStoreId(), UserHolder.getId())){
            return ResponseBuilder.fail("商家无权查看");
        }

        // Step 1: 解析时间范围
        LocalDate[] dateRange = resolveTimeRange(request.getTimeRange());
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];

        // Step 2: 调用服务层获取实际数据
        BigDecimal totalSales = salesStatsService.getTotalSales(request.getStoreId(), startDate, endDate);
        int orderCount = salesStatsService.getOrderCount(request.getStoreId(), startDate, endDate);
        List<Product> popularProducts = salesStatsService.getPopularProducts(request.getStoreId(), startDate, endDate);

        // Step 3: 封装响应
        SaleOverviewStatsResponse response = new SaleOverviewStatsResponse();
        response.setTotalSales(totalSales);
        response.setOrderCount(orderCount);
        response.setPopularProducts(popularProducts);

        System.out.println("响应参数: \n"
                + "总销量: " + totalSales + "\n"
                + "总订单数: " + orderCount + "\n");
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/trend")
    public CommonResponse getSalesTrend(
            @RequestHeader("Authorization") String tokenHeader,
            @Valid @RequestBody SaleTrendStatsRequest request
    ) {

        if(!storeService.validateStoreOwnership(request.getStoreId(), UserHolder.getId())){
            return ResponseBuilder.fail("商家无权查看");
        }

        // Step 1: 调用服务层获取趋势数据
        List<SaleTrendData> trendData = salesStatsService.getSalesTrend(
                request.getStoreId(),
                request.getType(),
                request.getNumOfRecentDays()
        );

        // Step 2: 提取日期标签和销售额
        List<String> dateLabels = trendData.stream()
                .map(SaleTrendData::getDateLabel)
                .collect(Collectors.toList());

        List<Integer> salesValues = trendData.stream()
                .map(data -> data.getValue().intValue())
                .collect(Collectors.toList());

        // Step 3: 封装响应
        SaleTrendStatsResponse response = new SaleTrendStatsResponse();
        response.setDates(dateLabels);
        response.setValues(salesValues);

        return ResponseBuilder.ok(response);
    }

    // 辅助方法：解析时间范围枚举为具体日期
    private LocalDate[] resolveTimeRange(SaleOverviewStatsRequest.TimeRange range) {
        LocalDate now = LocalDate.now();
        return switch (range) {
            case TODAY -> new LocalDate[]{now, now};
            case THIS_WEEK -> new LocalDate[]{now.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)), now};
            case THIS_MONTH -> new LocalDate[]{now.withDayOfMonth(1), now};
        };
    }
}