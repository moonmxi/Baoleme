package org.demo.baoleme.service.impl;

import org.demo.baoleme.mapper.*;
import org.demo.baoleme.pojo.*;
import org.demo.baoleme.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RiderMapper riderMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private StoreMapper storeMapper;

    @Override
    public Admin login(Long id, String password) {
        Admin admin = adminMapper.selectById(id);
        if (admin != null && password.equals(admin.getPassword())) {
            return admin;
        }
        return null;
    }

    @Override
    public Admin getInfo(Long id) {
        return adminMapper.selectById(id);
    }

    @Override
    public List<User> getAllUsersPaged(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return userMapper.selectUsersPaged(offset, pageSize);
    }

    @Override
    public List<Rider> getAllRidersPaged(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return riderMapper.selectRidersPaged(offset, pageSize);
    }

    @Override
    public List<Merchant> getAllMerchantsPaged(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return merchantMapper.selectMerchantsPaged(offset, pageSize);
    }

    @Override
    public List<Store> getAllStoresPaged(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return storeMapper.selectStoresPaged(offset, pageSize);
    }
}