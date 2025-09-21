package com.trick.marketing.service;

import com.trick.marketing.model.dto.UpdateCouponForUserDTO;
import com.trick.marketing.model.vo.CouponHubVO;
import com.trick.marketing.model.vo.wxCouponsVO.CouponBaseVO;

import java.util.List;

public interface UserCouponService {
    void addCouponToUser(Integer userId, Integer couponId);

    void updateCouponInformationForUser(Integer userId, Integer couponId, UpdateCouponForUserDTO dto);

    List<CouponBaseVO> getCoupons(Integer userId, Integer type, Integer couponId);

    List<CouponHubVO> getCouponHub();
}
