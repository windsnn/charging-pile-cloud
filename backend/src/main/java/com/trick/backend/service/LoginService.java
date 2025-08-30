package com.trick.backend.service;

import com.trick.backend.model.dto.LoginDTO;
import com.trick.backend.model.vo.WxLoginVO;

import java.util.Map;

public interface LoginService {
    //微信用户登录
    String loginUser(String code) throws Exception;

    //管理员用户登录
    String loginAdmin(LoginDTO dto);

}
