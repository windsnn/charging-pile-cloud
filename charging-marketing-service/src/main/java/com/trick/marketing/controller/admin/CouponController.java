package com.trick.marketing.controller.admin;

import com.trick.common.result.Result;
import com.trick.marketing.model.dto.AddCouponsDTO;
import com.trick.marketing.model.dto.QueryCouponsDTO;
import com.trick.marketing.model.dto.UpdateCouponsDTO;
import com.trick.marketing.model.vo.CouponsVO;
import com.trick.marketing.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/marketing/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;

    /**
     * 管理员添加优惠券
     *
     * @param addCouponsDTO 添加优惠券DTO
     * @return 统一返回结果result
     */
    @PostMapping
    public Result<?> addCoupon(@RequestBody AddCouponsDTO addCouponsDTO) {
        couponService.addCoupon(addCouponsDTO);
        return Result.success();
    }

    /**
     * 条件查询券 无条件则查询所有券
     *
     * @param dto QueryCouponsDTO
     * @return 统一返回 Result 泛型 List CouponsVO
     */
    @GetMapping
    public Result<List<CouponsVO>> getCoupons(QueryCouponsDTO dto) {
        return Result.success(couponService.getCoupons(dto));
    }

    /**
     * 更新优惠券状态
     *
     * @param couponId 优惠券ID
     * @param dto      更新数据
     * @return 统一
     */
    @PostMapping("/{id}")
    public Result<?> updateCoupon(@PathVariable("id") Integer couponId, @RequestBody UpdateCouponsDTO dto) {
        couponService.updateCoupon(couponId, dto);
        return Result.success();
    }

}
