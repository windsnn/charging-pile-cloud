package com.trick.marketing.controller.wechat;

import com.trick.common.result.Result;
import com.trick.common.utils.ThreadLocalUtil;
import com.trick.marketing.service.CouponService;
import com.trick.marketing.service.UserCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("wx/marketing/coupons")
public class WxCouponController {
    @Autowired
    private UserCouponService userCouponService;

    /**
     * 用户通过前端获取一张优惠券
     *
     * @return 统一返回
     */
    @GetMapping("/{id}")
    public Result<?> getCoupon(@PathVariable("id") Integer couponId) {
        Integer userId = ThreadLocalUtil.getUserId();

        userCouponService.addCouponToUser(userId, couponId);
        return Result.success();
    }

}
