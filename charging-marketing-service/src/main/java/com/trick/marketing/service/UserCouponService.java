package com.trick.marketing.service;

import com.trick.marketing.model.dto.UpdateCouponForUserDTO;
import com.trick.marketing.model.vo.wxCouponsVO.CouponBaseVO;

public interface UserCouponService {
    void addCouponToUser(Integer userId, Integer couponId);

    CouponBaseVO getCouponById(Integer userId, Integer couponId);

    void updateCouponInformationForUser(Integer userId, Integer couponId, UpdateCouponForUserDTO dto);

    CouponBaseVO getCoupons(Integer userId, Integer type);
}
