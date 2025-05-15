package org.demo.baoleme.service.impl;

import org.demo.baoleme.mapper.RiderMapper;
import org.demo.baoleme.pojo.Rider;
import org.demo.baoleme.service.RiderService;
import org.demo.baoleme.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RiderServiceImpl implements RiderService {

    @Autowired
    private RiderMapper riderMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Rider register(Rider rider) {
        if (!StringUtils.hasText(rider.getUsername()) || !StringUtils.hasText(rider.getPassword())) {
            return null;
        }

        // 用户名或手机号已存在
        if (riderMapper.selectByUsername(rider.getUsername()) != null ||
                (rider.getPhone() != null && riderMapper.selectByPhone(rider.getPhone()) != null)) {
            return null;
        }

        rider.setPassword(passwordEncoder.encode(rider.getPassword()));
        rider.setOrderStatus(-1);     // 未激活
        rider.setDispatchMode(1);     // 自动接单
        rider.setBalance(0L);

        return riderMapper.insert(rider) > 0 ? rider : null;
    }

    @Override
    public Rider login(String phone, String rawPassword) {
        Rider rider = riderMapper.selectByPhone(phone);
        if (rider == null || !passwordEncoder.matches(rawPassword, rider.getPassword())) {
            return null;
        }

        rider.setOrderStatus(1); // 登录后默认状态为空闲
        riderMapper.updateById(rider);
        return rider;
    }

    @Override
    public Rider getInfo(Long riderId) {
        return riderMapper.selectById(riderId);
    }

    @Override
    public boolean updateInfo(Rider rider) {
        if (rider == null || rider.getId() == null) return false;

        Rider existing = riderMapper.selectById(rider.getId());
        if (existing == null) return false;

        if (rider.getUsername() != null) {
            existing.setUsername(rider.getUsername());
        }
        if (rider.getPassword() != null) {
            existing.setPassword(passwordEncoder.encode(rider.getPassword()));
        }
        if (rider.getPhone() != null) {
            existing.setPhone(rider.getPhone());
        }
        if (rider.getDispatchMode() != null) {
            existing.setDispatchMode(rider.getDispatchMode());
        }
        if (rider.getOrderStatus() != null) {
            existing.setOrderStatus(rider.getOrderStatus());
        }

        return riderMapper.updateById(existing) > 0;
    }

    @Override
    public boolean delete(Long riderId) {
        return riderMapper.deleteById(riderId) > 0;
    }
}