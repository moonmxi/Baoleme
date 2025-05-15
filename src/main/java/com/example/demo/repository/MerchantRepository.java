package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Merchant;

// 数据访问类：负责数据库操作
@Repository
public class MerchantRepository {
    public Merchant findByUsername(String username) {
        // 实际数据库查询逻辑（如JDBC/JPA）
        // 若使用 Spring Data JPA，MerchantRepository 接口可直接继承 JpaRepository，利用内置的 save 方法
        // ???
        String password = queryPasswordFromDB(username);
        return new Merchant(username, password);
    }

    private String queryPasswordFromDB(@SuppressWarnings("unused") String username) {
        // 示例伪代码：从数据库查询密码
        return Merchant.DEFAULTPASSWORD; 
    }
}