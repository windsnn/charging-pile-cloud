package com.trick.admin.service.impl;

import com.trick.admin.mapper.LoginMapper;
import com.trick.admin.model.dto.AdminDTO;
import com.trick.admin.model.pojo.Admin;
import com.trick.admin.service.LoginService;
import com.trick.common.exception.BusinessException;
import com.trick.common.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String loginAdmin(AdminDTO adminDTO) {
        // 根据用户名查询用户
        Admin admin = loginMapper.getAdminByUsername(adminDTO.getUsername());
        if (admin == null) {
            throw new BusinessException("没有该用户");
        }

        // 验证密码是否正确（可优化使用加密方式）
        if (!admin.getPassword().equals(adminDTO.getPassword())) {
            throw new BusinessException("密码错误");
        }

        //获取adminToken
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", admin.getId());
        claims.put("username", admin.getUsername());
        claims.put("role", "admin");

        return jwtUtil.getToken(claims);
    }
}
