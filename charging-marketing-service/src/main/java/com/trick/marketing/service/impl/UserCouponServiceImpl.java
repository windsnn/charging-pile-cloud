package com.trick.marketing.service.impl;

import com.trick.marketing.mapper.UserCouponMapper;
import com.trick.marketing.service.UserCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCouponServiceImpl implements UserCouponService {
    @Autowired
    private UserCouponMapper userCouponMapper;

    //todo 用户优惠券中心 获取所有可用优惠券

    //todo 查询我的所有优惠券

    //todo 根据优惠券ID查询该优惠券部分信息

    /**
     * 用户添加优惠券到账户
     *
     * @param userId 用户ID
     * @param couponId   优惠券号码
     */
    @Override
    public void addCouponToUser(Integer userId, Integer couponId) {
        //todo


        userCouponMapper.addCouponToUser(userId, couponId);
    }
}
