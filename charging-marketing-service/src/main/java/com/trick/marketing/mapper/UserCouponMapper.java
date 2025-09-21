package com.trick.marketing.mapper;

import com.trick.marketing.model.dto.CouponsDTO;
import com.trick.marketing.model.dto.UpdateCouponForUserDTO;
import com.trick.marketing.model.pojo.Coupons;
import com.trick.marketing.model.pojo.UserCoupons;
import com.trick.marketing.model.vo.CouponHubVO;
import com.trick.marketing.model.vo.wxCouponsVO.CouponBaseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserCouponMapper {

    //用户账户添加优惠券
    void addCouponToUser(UserCoupons userCoupons);

    //用户是否领取过优惠券
    boolean hasUserClaimedCoupon(Integer userId, Integer couponId);

    //更新优惠券信息
    void updateCouponInformationForUser(@Param("userId") Integer userId,
                                        @Param("couponId") Integer couponId,
                                        @Param("dto") UpdateCouponForUserDTO dto);

    List<CouponsDTO> getCoupons(@Param("userId") Integer userId,
                                @Param("type") Integer type,
                                @Param("couponId") Integer couponId);

    List<CouponHubVO> getCouponHub();
}
