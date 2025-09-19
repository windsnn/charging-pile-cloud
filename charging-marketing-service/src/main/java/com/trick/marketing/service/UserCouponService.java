package com.trick.marketing.service;

import com.trick.common.result.Result;
import com.trick.marketing.model.vo.wxCouponsVO.CouponBaseVO;

public interface UserCouponService {
    void addCouponToUser(Integer userId, Integer couponId);

    CouponBaseVO getCouponById(Integer userId, Integer couponId);

    boolean hasUserClaimedCoupon(Integer userId, Integer couponId);

    //更新用户优惠券状态
    Integer updateCouponStatus(Integer userId, Integer couponId, Integer status);
}
