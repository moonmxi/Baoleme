package org.demo.baoleme.service;

import org.demo.baoleme.pojo.Admin;

public interface AdminService {

    /**
     * 登录验证管理员账号密码
     */
    Admin login(Long id, String password);

    /**
     * 查询管理员信息（可用于 /info 接口）
     */
    Admin getInfo(Long id);
}