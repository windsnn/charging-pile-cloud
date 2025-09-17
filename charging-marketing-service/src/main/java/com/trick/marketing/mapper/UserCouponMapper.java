package com.trick.marketing.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCouponMapper {

    //用户账户添加优惠券
    void addCouponToUser(Integer userId, Integer couponId);
}
