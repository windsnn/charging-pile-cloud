package com.trick.marketing.service;

import com.trick.marketing.model.dto.AddCouponsDTO;
import com.trick.marketing.model.dto.QueryCouponsDTO;
import com.trick.marketing.model.vo.CouponsVO;

import java.util.List;

public interface CouponService {
    void addCoupon(AddCouponsDTO addCouponsDTO);

    List<CouponsVO> getCoupons(QueryCouponsDTO dto);
}
