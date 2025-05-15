package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Merchant;
import com.example.demo.mapper.MerchantMapper;
import com.example.demo.service.MerchantService;

@Service
@Transactional // 声明式事务管理
public class MerchantServiceImpl implements MerchantService {

    private final MerchantMapper merchantMapper;

    @Autowired
    public MerchantServiceImpl(MerchantMapper merchantMapper) {
        this.merchantMapper = merchantMapper;
    }

    @Override
    public Merchant createMerchant(Merchant merchant) {
        merchantMapper.insertMerchant(merchant); // MyBatis自动回填id
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
    public void deleteMerchant(Long id) {
        merchantMapper.deleteById(id);
    }
}
