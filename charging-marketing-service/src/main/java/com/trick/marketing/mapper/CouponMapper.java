package com.trick.marketing.mapper;

import com.trick.marketing.model.dto.AddCouponsDTO;
import com.trick.marketing.model.dto.QueryCouponsDTO;
import com.trick.marketing.model.dto.UpdateCouponsDTO;
import com.trick.marketing.model.pojo.Coupons;
import com.trick.marketing.model.vo.CouponsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponMapper {
    //添加券
    void addCoupon(AddCouponsDTO addCouponsDTO);

    //查询券，条件
    List<CouponsVO> getCoupons(QueryCouponsDTO dto);

    Coupons getCouponById(Integer couponId);

    int updateStock(Integer couponId);

    Integer updateCoupon(@Param("couponId") Integer couponId,
                         @Param("dto") UpdateCouponsDTO dto);

    void updateExpiredCoupon();
}
