package org.demo.baoleme.service.impl;

import org.demo.baoleme.mapper.MerchantMapper;
import org.demo.baoleme.pojo.Merchant;
import org.demo.baoleme.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional // 声明式事务管理
public class MerchantServiceImpl implements MerchantService {
    private final MerchantMapper merchantMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public MerchantServiceImpl(MerchantMapper merchantMapper) {
        this.merchantMapper = merchantMapper;
    }

    @Override
    public Merchant createMerchant(Merchant merchant) {
        // 用户名或密码为空
        if (!StringUtils.hasText(merchant.getUsername()) || !StringUtils.hasText(merchant.getPassword())) {
            return null;
        }

        // 用户名或手机号已存在
        if (merchantMapper.selectByUsername(merchant.getUsername()) != null ||
                (merchant.getPhone() != null && merchantMapper.selectByPhone(merchant.getPhone()) != null)) {
            return null;
        }

        merchant.setPassword(passwordEncoder.encode(merchant.getPassword()));

        merchantMapper.insert(merchant); // MyBatis自动回填id
        return merchant;
    }

    @Override
    public Merchant getMerchantById(Long id) {
        return merchantMapper.selectById(id);
    }

    @Override
    public Merchant getMerchantByUsername(String username) {
        return merchantMapper.selectByUsername(username);
    }

    @Override
    public List<Merchant> getAllMerchants() {
        return merchantMapper.selectAll();
    }

    @Override
    public Merchant updateMerchant(Merchant merchant) {
        merchantMapper.updateMerchant(merchant);
        return merchantMapper.selectById(merchant.getId());
    }

    @Override
    public boolean deleteMerchant(Long id) {
        return merchantMapper.deleteById(id) > 0;
    }
}
