package org.demo.baoleme.service.impl;

import org.demo.baoleme.mapper.AdminMapper;
import org.demo.baoleme.pojo.Admin;
import org.demo.baoleme.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

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
}