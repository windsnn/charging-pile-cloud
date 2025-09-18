package com.trick.marketing.service;

import com.trick.common.result.Result;
import com.trick.marketing.model.vo.wxCouponsVO.CouponBaseVO;

public interface UserCouponService {
    void addCouponToUser(Integer userId, Integer couponId);

    CouponBaseVO getCouponById(Integer userId, Integer couponId);
}
