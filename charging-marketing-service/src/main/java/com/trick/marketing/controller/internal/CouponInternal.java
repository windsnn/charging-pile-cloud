package com.trick.marketing.controller.internal;

import com.trick.common.result.Result;
import com.trick.common.utils.ThreadLocalUtil;
import com.trick.marketing.model.dto.UpdateCouponForUserDTO;
import com.trick.marketing.service.UserCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/wx/marketing/coupons")
public class CouponInternal {
    @Autowired
    private UserCouponService userCouponService;

    /**
     * 内部接口 更新用户优惠券信息
     *
     * @param couponId 优惠券ID
     * @param dto UpdateCouponForUserDTO
     * @return 统一
     */
    @PostMapping("/{id}")
    Result<?> updateCouponInformationForUser(@PathVariable("id") Integer couponId,
                                             @RequestBody UpdateCouponForUserDTO dto) {
        Integer userId = ThreadLocalUtil.getUserId();

        userCouponService.updateCouponInformationForUser(userId, couponId, dto);
        return Result.success();
    }
}
