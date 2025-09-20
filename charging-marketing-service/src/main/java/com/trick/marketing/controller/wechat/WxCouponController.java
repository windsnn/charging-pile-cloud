package com.trick.marketing.controller.wechat;

import com.trick.common.result.Result;
import com.trick.common.utils.ThreadLocalUtil;
import com.trick.marketing.model.vo.wxCouponsVO.CouponBaseVO;
import com.trick.marketing.service.UserCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wx/marketing/coupons")
public class WxCouponController {
    @Autowired
    private UserCouponService userCouponService;

    /**
     * 用户通过前端将一张优惠券添加进背包
     *
     * @return 统一返回
     */
    @PostMapping("/receive")
    public Result<?> receiveCoupon(@RequestParam("id") Integer couponId) {
        Integer userId = ThreadLocalUtil.getUserId();

        userCouponService.addCouponToUser(userId, couponId);
        return Result.success();
    }

    /**
     * 获取用户的优惠券列表（个人页面），根据优惠券类型
     *
     * @param type 优惠券类型
     * @return CouponBaseVO的不同子类
     */
    @GetMapping("/{type}")
    public Result<CouponBaseVO> getCoupons(@PathVariable("type") Integer type) {
        Integer userId = ThreadLocalUtil.getUserId();
        return Result.success(userCouponService.getCoupons(userId, type));
    }

    //todo 获取已经启用的优惠券列表（优惠券中心）


    /**
     * 通过优惠券ID和用户ID，获取该优惠券的部分信息
     *
     * @param couponId 优惠券ID
     * @return 部分信息
     */
    @GetMapping("/{id}")
    public Result<CouponBaseVO> getCouponById(@PathVariable("id") Integer couponId) {
        Integer userId = ThreadLocalUtil.getUserId();
        return Result.success(userCouponService.getCouponById(userId, couponId));
    }

}
