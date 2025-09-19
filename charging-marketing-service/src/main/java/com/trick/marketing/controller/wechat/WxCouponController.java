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

    //todo  分页获取用户的优惠券列表（个人页面）
    //todo 分页获取已经启用的优惠券列表（优惠券中心）


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

    /**
     * 查询该用户是否拥有该优惠券ID
     *
     * @param couponId 优惠券ID
     * @return 是否
     * @deprecated 暂时不需要使用
     */
    @GetMapping("/ownership/{id}")
    public Result<Boolean> hasUserClaimedCoupon(@PathVariable("id") Integer couponId) {
        Integer userId = ThreadLocalUtil.getUserId();
        return Result.success(userCouponService.hasUserClaimedCoupon(userId, couponId));
    }

    /**
     * 更新用户优惠券状态
     *
     * @param couponId 优惠券ID
     * @param status   优惠券状态（0/1/2 未使用/已使用/已过期）
     * @return 统一格式
     */
    @PutMapping("/status/{id}")
    public Result<Integer> updateCouponStatus(@PathVariable("id") Integer couponId, @RequestParam("status") Integer status) {
        Integer userId = ThreadLocalUtil.getUserId();
        return Result.success(userCouponService.updateCouponStatus(userId, couponId, status));
    }
}
