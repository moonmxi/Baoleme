package org.demo.baoleme.service.impl;

import org.demo.baoleme.mapper.RiderMapper;
import org.demo.baoleme.pojo.Rider;
import org.demo.baoleme.service.RiderService;
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
        // 基本字段不能为空
        if (!StringUtils.hasText(rider.getUsername())) {
            System.out.println("注册失败：用户名为空");
            return null;
        }
        if (!StringUtils.hasText(rider.getPassword())) {
            System.out.println("注册失败：密码为空");
            return null;
        }

        // 校验用户名是否已存在
        if (riderMapper.selectByUsername(rider.getUsername()) != null) {
            System.out.println("注册失败：用户名已存在");
            return null;
        }

        // 校验手机号是否已存在
        if (StringUtils.hasText(rider.getPhone()) && riderMapper.selectByPhone(rider.getPhone()) != null) {
            System.out.println("注册失败：手机号已注册");
            return null;
        }

        // 初始化字段
        rider.setPassword(passwordEncoder.encode(rider.getPassword()));
        rider.setOrderStatus(-1);   // -1 表示未激活
        rider.setDispatchMode(1);   // 默认开启自动接单
        rider.setBalance(0L);

        boolean inserted = riderMapper.insert(rider) > 0;
        return inserted ? rider : null;
    }

    @Override
    public Rider login(String phone, String rawPassword) {
        Rider rider = riderMapper.selectByPhone(phone);
        if (rider == null) {
            System.out.println("登录失败：手机号不存在");
            return null;
        }

        if (!passwordEncoder.matches(rawPassword, rider.getPassword())) {
            System.out.println("登录失败：密码错误");
            return null;
        }

        // 登录成功，设置空闲状态
        rider.setOrderStatus(1);
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

        if (StringUtils.hasText(rider.getUsername())) {
            existing.setUsername(rider.getUsername());
        }
        if (StringUtils.hasText(rider.getPassword())) {
            existing.setPassword(passwordEncoder.encode(rider.getPassword()));
        }
        if (StringUtils.hasText(rider.getPhone())) {
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