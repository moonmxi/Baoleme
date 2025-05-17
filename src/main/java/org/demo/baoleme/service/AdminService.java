package org.demo.baoleme.service;

import org.demo.baoleme.pojo.Admin;

public interface AdminService {

    /**
     * 登录验证，返回 token
     */
    String login(Long adminId, String password);

    /**
     * 登出操作（清除 Redis 中的 token）
     */
    boolean logout(Long adminId);
}