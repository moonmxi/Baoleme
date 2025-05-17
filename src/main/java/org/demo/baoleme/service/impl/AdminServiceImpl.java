package org.demo.baoleme.service.impl;

import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.mapper.AdminMapper;
import org.demo.baoleme.pojo.Admin;
import org.demo.baoleme.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String REDIS_KEY_PREFIX = "admin:token:";

    @Override
    public String login(Long adminId, String password) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null || !admin.getPassword().equals(password)) {
            return null;
        }

        String token = JwtUtils.createToken(adminId, "admin", null);
        String redisKey = REDIS_KEY_PREFIX + adminId;
        redisTemplate.opsForValue().set(redisKey, token, 12, TimeUnit.HOURS);
        return token;
    }

    @Override
    public boolean logout(Long adminId) {
        String redisKey = REDIS_KEY_PREFIX + adminId;
        Boolean deleted = redisTemplate.delete(redisKey);
        return Boolean.TRUE.equals(deleted);
    }
}