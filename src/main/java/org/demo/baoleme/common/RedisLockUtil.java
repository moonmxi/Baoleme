package org.demo.baoleme.common;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLockUtil {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisLockUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean tryLock(String key, String value, long timeout, TimeUnit unit) {
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        return Boolean.TRUE.equals(success);
    }

    public void unlock(String key, String value) {
        // 这里简单处理，生产场景用 Lua 脚本保证原子性
        String current = stringRedisTemplate.opsForValue().get(key);
        if (value.equals(current)) {
            stringRedisTemplate.delete(key);
        }
    }
}