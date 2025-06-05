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
@Transactional
public class MerchantServiceImpl implements MerchantService {
    private final MerchantMapper merchantMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public MerchantServiceImpl(MerchantMapper merchantMapper) {
        this.merchantMapper = merchantMapper;
    }

    /* ========================= 插入操作 ========================= */
    @Override
    public Merchant createMerchant(Merchant merchant) {
        // Step1: 验证基础参数有效性
        if (merchant == null
                || !StringUtils.hasText(merchant.getUsername())
                || !StringUtils.hasText(merchant.getPassword())
                || !StringUtils.hasText(merchant.getPhone())
        ) {
            return null;
        }

        // Step2: 检查业务唯一性约束
        if (isUsernameExists(merchant.getUsername()) || isPhoneExists(merchant.getPhone())) {
            return null;
        }

        // Step3: 敏感数据处理
        merchant.setPassword(passwordEncoder.encode(merchant.getPassword()));

        // Step4: 执行持久化操作
        merchantMapper.insert(merchant);

        // Step5: 返回创建结果
        return merchant;
    }

    /* ========================= 查询操作 ========================= */
    @Override
    public Merchant getMerchantById(Long id) {
        // Step1: 直接执行ID查询
        return merchantMapper.selectById(id);
    }

    @Override
    public Merchant getMerchantByUsername(String username) {
        // Step1: 参数有效性检查
        if (!StringUtils.hasText(username)) return null;

        // Step2: 执行用户名查询
        return merchantMapper.selectByUsername(username);
    }

    @Override
    public Merchant getMerchantByPhone(String phone) {
        // Step1: 参数有效性检查
        if (!StringUtils.hasText(phone)) return null;

        // Step2: 执行手机号查询
        return merchantMapper.selectByPhone(phone);
    }

    @Override
    public List<Merchant> getAllMerchants() {
        // Step1: 执行全量查询
        return merchantMapper.selectAll();
    }

    /* ========================= 更新操作 ========================= */
    @Override
    public Merchant updateInfo(Merchant merchant) {
        // Step1: 验证主键参数有效性
        if (merchant == null || merchant.getId() == null) return null;

        // Step2: 获取持久化对象
        Merchant existing = merchantMapper.selectById(merchant.getId());
        if (existing == null) return null;

        // Step3: 校验用户名冲突
        if (hasUsernameConflict(merchant, existing)) return null;

        // Step4: 安全更新字段
        applyUpdates(merchant, existing);

        // Step5: 执行数据库更新
        merchantMapper.updateMerchant(existing);

        // Step6: 返回最新数据
        return merchantMapper.selectById(merchant.getId());
    }

    /* ========================= 删除操作 ========================= */
    @Override
    public boolean deleteMerchant(Long id) {
        // Step1: 执行删除操作
        int affectedRows = merchantMapper.deleteById(id);

        // Step2: 返回操作结果
        return affectedRows > 0;
    }

    /* ------------------------- 私有方法 ------------------------- */
    /**
     * 校验用户名冲突（支持更新时的自身ID排除）
     */
    private boolean hasUsernameConflict(Merchant newData, Merchant existing) {
        // Step1: 检查是否需要校验用户名
        if (!StringUtils.hasText(newData.getUsername())) return false;
        if (existing.getUsername().equals(newData.getUsername())) return false;

        // Step2: 执行数据库查询
        Merchant conflict = merchantMapper.selectByUsername(newData.getUsername());
        return conflict != null;
    }

    /**
     * 安全字段更新策略
     */
    private void applyUpdates(Merchant source, Merchant target) {
        // Step1: 更新用户名
        if (StringUtils.hasText(source.getUsername())) {
            target.setUsername(source.getUsername());
        }

        // Step2: 更新密码（带加密）
        if (StringUtils.hasText(source.getPassword())) {
            target.setPassword(passwordEncoder.encode(source.getPassword()));
        }

        // Step3: 更新手机号
        if (StringUtils.hasText(source.getPhone())) {
            target.setPhone(source.getPhone());
        }

        // Step4:
        if (StringUtils.hasText(source.getAvatar())) {
            target.setAvatar(source.getAvatar());
        }
    }

    /**
     * 检查用户名是否存在
     */
    private boolean isUsernameExists(String username) {
        return StringUtils.hasText(username) &&
                merchantMapper.selectByUsername(username) != null;
    }

    /**
     * 检查手机号是否存在
     */
    private boolean isPhoneExists(String phone) {
        return StringUtils.hasText(phone) &&
                merchantMapper.selectByPhone(phone) != null;
    }

    @Override
    public boolean updateAvatar(Long merchantId, String avatarPath) {
        if (merchantId == null || !StringUtils.hasText(avatarPath)) {
            return false;
        }
        int rows = merchantMapper.updateAvatarById(merchantId, avatarPath);
        return rows > 0;
    }
}