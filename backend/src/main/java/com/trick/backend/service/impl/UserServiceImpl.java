package com.trick.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.trick.backend.common.result.PageResult;
import com.trick.backend.common.utils.WxPhoneDecryptUtil;
import com.trick.backend.mapper.UserMapper;
import com.trick.backend.model.dto.UserAddAndUpdateDTO;
import com.trick.backend.model.dto.UserQueryDTO;
import com.trick.backend.model.pojo.User;
import com.trick.backend.model.vo.UserVO;
import com.trick.backend.model.vo.WxUserProfileVO;
import com.trick.backend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final String WX_SESSION_KEY_PREFIX = "wx:sessionkey:";
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WxPhoneDecryptUtil wxPhoneDecryptUtil;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //分页条件查询
    @Override
    public PageResult<User> getUsersByPage(UserQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<User> list = userMapper.getAllUsers(queryDTO);
        PageInfo<User> pageInfo = new PageInfo<>(list);

        List<User> records = pageInfo.getList();
        long total = pageInfo.getTotal();

        return new PageResult<>(total, records);
    }

    //返回管理系统User信息
    @Override
    public UserVO getUserById(Integer id) {
        UserVO userVO = new UserVO();
        User user = getUser(id);

        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    //返回微信个人中心User信息
    @Override
    @Cacheable(value = "user", key = "#userId")
    public WxUserProfileVO getUserProfileById(Integer userId) {
        WxUserProfileVO wxUserProfileVO = new WxUserProfileVO();
        User user = getUser(userId);

        BeanUtils.copyProperties(user, wxUserProfileVO);
        return wxUserProfileVO;
    }

    //根据openId查询用户ID
    @Override
    public Integer getUserByOpenid(String openid) {
        return userMapper.getUserByOpenId(openid);
    }

    //微信用户注册
    @Override
    public Integer addUser(UserAddAndUpdateDTO dto) {
        userMapper.addUser(dto);
        return dto.getId();
    }

    //微信用户数据更新
    @Override
    @CacheEvict(value = "user", key = "#dto.id")
    public void updateUser(UserAddAndUpdateDTO dto) {
        String encryptedDate = dto.getEncryptedDate();
        String iv = dto.getIv();

        if (encryptedDate != null && iv != null) {
            String sessionKey = redisTemplate.opsForValue().get(WX_SESSION_KEY_PREFIX + dto.getId());
            String phone = wxPhoneDecryptUtil.decryptPhoneNumber(encryptedDate, sessionKey, iv);
            dto.setPhone(phone);
        }
        System.out.println(dto.getPhone());
        userMapper.updateUser(dto);
    }

    //获取个人余额
    @Override
    public BigDecimal getWallet(Integer id) {
        return userMapper.getWallet(id);
    }

    //更新账户余额
    @Override
    public void addBalance(Integer id, BigDecimal amount) {
        userMapper.addBalance(id, amount);
    }

    //======================内部方法=============================

    //根据ID查询User信息
    private User getUser(Integer id) {
        return userMapper.getUserById(id);
    }

}
