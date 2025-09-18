package com.trick.marketing.mapper;

import com.trick.marketing.model.dto.CouponsDTO;
import com.trick.marketing.model.pojo.Coupons;
import com.trick.marketing.model.pojo.UserCoupons;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCouponMapper {

    //用户账户添加优惠券
    void addCouponToUser(UserCoupons userCoupons);

    //用户是否领取优惠券
    boolean hasUserClaimedCoupon(Integer userId, Integer couponId);

    //根据ID获取优惠券
    CouponsDTO getCouponById(Integer userId, Integer couponId);
}
