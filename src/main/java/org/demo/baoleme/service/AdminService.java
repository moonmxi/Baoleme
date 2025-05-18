package org.demo.baoleme.service;

import org.demo.baoleme.pojo.*;

import java.util.List;

public interface AdminService {

    /**
     * 登录验证管理员账号密码
     */
    Admin login(Long id, String password);

    /**
     * 查询管理员信息（可用于 /info 接口）
     */
    Admin getInfo(Long id);

    List<User> getAllUsersPaged(int page, int pageSize);

    /**
     * 分页查询所有骑手
     */
    List<Rider> getAllRidersPaged(int page, int pageSize);

    List<Merchant> getAllMerchantsPaged(int page, int pageSize);

    List<Store> getAllStoresPaged(int page, int pageSize);
}