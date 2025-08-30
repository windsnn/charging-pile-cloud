package com.trick.backend.service;

import com.trick.backend.common.result.PageResult;
import com.trick.backend.model.dto.UserAddAndUpdateDTO;
import com.trick.backend.model.dto.UserQueryDTO;
import com.trick.backend.model.pojo.User;
import com.trick.backend.model.vo.UserVO;
import com.trick.backend.model.vo.WxUserProfileVO;

import java.math.BigDecimal;

public interface UserService {
    //分页条件查询
    PageResult<User> getUsersByPage(UserQueryDTO queryDTO);

    //根据ID查询
    UserVO getUserById(Integer id);

    WxUserProfileVO getUserProfileById(Integer userId);

    //根据openId查询用户ID
    Integer getUserByOpenid(String openid);

    //进行微信用户注册,返回ID
    Integer addUser(UserAddAndUpdateDTO dto);

    //用户的数据更新
    void updateUser(UserAddAndUpdateDTO userAddAndUpdateDTO);

    BigDecimal getWallet(Integer userId);

    void addBalance(Integer id, BigDecimal amount);
}
