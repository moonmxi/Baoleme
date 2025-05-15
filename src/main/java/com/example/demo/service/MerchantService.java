package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Merchant;

public interface MerchantService {
    // 创建商户
    Merchant createMerchant(Merchant merchant);
    // 根据ID查询商户
    Merchant getMerchantById(Long id);
    Merchant getMerchantByUsername(String username);
    // 查询所有商户
    List<Merchant> getAllMerchants();
    // 更新商户信息
    Merchant updateMerchant(Merchant merchant);
    // 删除商户
    void deleteMerchant(Long id);
}