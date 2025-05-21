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

    /* 插入版块 */
    /**
     * 新建一个merchant，并将其插入数据库
     * @param merchant 新商家
     * @return 新增的merchant实例 如果插入成功 或 null 如果插入失败
     */
    @Override
    public Merchant createMerchant(Merchant merchant) {
        // Step1: 检查用户名或密码是否为空
        if (!StringUtils.hasText(merchant.getUsername()) || !StringUtils.hasText(merchant.getPassword())) {
            return null;
        }

        // Step2: 检查用户名或手机号是否已存在
        if (merchantMapper.selectByUsername(merchant.getUsername()) != null ||
                (merchant.getPhone() != null && merchantMapper.selectByPhone(merchant.getPhone()) != null)) {
            return null;
        }

        // Step3: 密码加密
        merchant.setPassword(passwordEncoder.encode(merchant.getPassword()));

        // Step4: 调用insert方法
        merchantMapper.insert(merchant); // MyBatis自动回填id

        // Step5: 返回create结果
        return merchant;
    }

    /* 查询版块 */
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

    /* 更新版块 */
    /**
     * 更新商家数据库。通过id查找需更新的商家
     * @param merchant 新商家
     * @return 更新后的merchant实例 或 null，如果更新失败
     */
    @Override
    public Merchant updateInfo(Merchant merchant) {
        // Step1: 检查传入参数是否非空
        if (merchant == null || merchant.getId() == null) return null;

        // Step2: 检查更新对象是否存在数据库
        Merchant existing = merchantMapper.selectById(merchant.getId());
        if (existing == null) return null;

        // Step3: 用户名重复校验
        // 当新用户名非空且与原用户名不同时，检查是否被其他商户占用
        if (StringUtils.hasText(merchant.getUsername())
                && !existing.getUsername().equals(merchant.getUsername())) {
            Merchant conflictMerchant = merchantMapper.selectByUsername(merchant.getUsername());
            if (conflictMerchant != null) {
                return null; // 用户名已被占用
            }
        }

        // Step4: 字段非空则更新。密码加密
        if (StringUtils.hasText(merchant.getUsername())) {
            existing.setUsername(merchant.getUsername());
        }
        if (StringUtils.hasText(merchant.getPassword())) {
            existing.setPassword(passwordEncoder.encode(merchant.getPassword()));
        }
        if (StringUtils.hasText(merchant.getPhone())) {
            existing.setPhone(merchant.getPhone());
        }

        // Step5: 更新至数据库，并返回结果
        merchantMapper.updateMerchant(existing);
        return merchantMapper.selectById(merchant.getId());
    }

    /* 删除版块 */
    @Override
    public boolean deleteMerchant(Long id) {
        return merchantMapper.deleteById(id) > 0;
    }
}