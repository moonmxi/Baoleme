package com.example.demo.repository;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Shop;

@Repository
public class ShopRepository {
    private static final String[] SHOP_TYPES = {"Book Store", "Cafe", "Tech Shop", "Clothing Store"};
    private static final String[] ADJECTIVES = {"Best", "Premium", "Luxury", "Affordable"};
    private static final Random random = new Random();

    public Shop findByMerchantName(String name) {
        // 如果传入空值返回null
        if (name == null || name.isBlank()) return null;

        // 动态生成随机数据
        return new Shop(
            generateShopId(),
            generateShopName(),
            generateDescription(),
            generateRandomDate(),
            name // 保持商户名称与传入参数一致
        );
    }

    // 并非随机。小问题，算了
    private Long generateShopId() {
        return Shop.sampleStoreIds[random.nextInt(Shop.sampleStoreIds.length)];
    }

    private String generateShopName() {
        return SHOP_TYPES[random.nextInt(SHOP_TYPES.length)] + " " + 
               (random.nextInt(900) + 100); // 添加随机编号
    }

    private String generateDescription() {
        return ADJECTIVES[random.nextInt(ADJECTIVES.length)] + " " +
               SHOP_TYPES[random.nextInt(SHOP_TYPES.length)] + " in the city";
    }

    private LocalDate generateRandomDate() {
        // 生成过去1-5年内的随机日期
        long minDay = LocalDate.now().minusYears(5).toEpochDay();
        long maxDay = LocalDate.now().minusYears(1).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }
}
