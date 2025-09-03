package com.trick.admin.service;

import com.trick.admin.model.dto.AdminDTO;

public interface LoginService {
    //管理员用户登录
    String loginAdmin(AdminDTO adminDTO);
}
