package com.example.demo.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Shop {
    // 预定义测试店铺ID（可根据需要扩展）
    public static Long[] sampleStoreIds = {1001L, 1002L, 1003L, 1004L};

    private Long id;
    private String name;
    private String description;
    private LocalDate createdDate;
    private String merchantName;

    // 无参构造器（可选，根据框架需求）
    public Shop() {
    }

    // 全参构造器
    public Shop(Long id, String name, String description, LocalDate createdDate, String merchantName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdDate = createdDate;
        this.merchantName = merchantName;
    }

    // Getter & Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // 直接返回格式化后的日期字符串
    public String getCreatedDate() {
        return createdDate.format(DateTimeFormatter.ISO_DATE);
    }

    // 可选的LocalDate原始类型访问
    public LocalDate getCreatedDateRaw() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public Long getId() {
        return id;
    }
}
