package com.trick.user.service;


public interface LoginService {
    //微信用户登录
    String loginUser(String code) throws Exception;
}
