package org.demo.baoleme.service;

import org.demo.baoleme.pojo.Merchant;

import java.util.List;

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
    boolean deleteMerchant(Long id);

    interface UserService {
    }
}
