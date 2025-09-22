package com.trick.marketing.service.impl;

import com.trick.common.exception.BusinessException;
import com.trick.marketing.enums.CouponStatus;
import com.trick.marketing.mapper.CouponMapper;
import com.trick.marketing.mapper.UserCouponMapper;
import com.trick.marketing.model.dto.AddCouponsDTO;
import com.trick.marketing.model.dto.QueryCouponsDTO;
import com.trick.marketing.model.dto.UpdateCouponsDTO;
import com.trick.marketing.model.pojo.Coupons;
import com.trick.marketing.model.vo.CouponsVO;
import com.trick.marketing.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {
    private static final int DISCOUNT_COUPON = 1; //折扣券
    private static final int VOUCHER = 2; //代金券

    private static final int FIXED_DATE = 1; //固定日期
    private static final int DYNAMIC_DATE = 2; //动态日期

    @Autowired
    private CouponMapper couponMapper;

    /**
     * 管理员添加券
     *
     * @param addCouponsDTO 券DTO
     */
    @Override
    public void addCoupon(AddCouponsDTO addCouponsDTO) {
        //1 验证数据
        switch (addCouponsDTO.getType()) {
            case DISCOUNT_COUPON -> verifyDiscountCoupon(addCouponsDTO);
            case VOUCHER -> verifyVoucher(addCouponsDTO);
            default -> throw new BusinessException("不存在的券类型");
        }

        //2 调用数据库添加券
        couponMapper.addCoupon(addCouponsDTO);
    }

    /**
     * 管理员条件获取优惠券全部信息
     *
     * @param dto QueryCouponsDTO
     * @return List.CouponsVO
     */
    @Override
    public List<CouponsVO> getCoupons(QueryCouponsDTO dto) {
        return couponMapper.getCoupons(dto);
    }


    /**
     * 根据优惠券ID查询优惠券全部信息
     * 获取的是优惠券的全部信息
     *
     * @param couponId 优惠券ID
     * @return 优惠券实体
     */
    @Override
    public Coupons getCouponById(Integer couponId) {
        return couponMapper.getCouponById(couponId);
    }

    /**
     * 原子更新库存
     *
     * @param couponId 优惠券ID
     */
    @Override
    public void updateStock(Integer couponId) {
        couponMapper.updateStock(couponId);
    }

    /**
     * 更新优惠券状态
     *
     * @param couponId 优惠券ID
     * @param dto      要更改的状态等数据
     */
    @Override
    public void updateCoupon(Integer couponId, UpdateCouponsDTO dto) {
        //验证状态合法性
        CouponStatus.of(dto.getStatus());

        //执行更新
        Integer updated = couponMapper.updateCoupon(couponId, dto);
        if (updated == 0) {
            throw new BusinessException("禁止更新已过期优惠券");
        }
    }

    //------------------------券类型验证---------------------------

    /**
     * 折扣券类型验证
     *
     * @param addCouponsDTO 券DTO
     */
    private void verifyDiscountCoupon(AddCouponsDTO addCouponsDTO) {
        //验证是否符合折扣券
        if (addCouponsDTO.getDiscountPercent() == null
                && addCouponsDTO.getMaxDiscountAmount() == null) {
            throw new BusinessException("折扣率或最大折扣金额为空");
        }

        verifyValidation(addCouponsDTO);
    }

    /**
     * 代金券类型验证
     *
     * @param addCouponsDTO 券DTO
     */
    private void verifyVoucher(AddCouponsDTO addCouponsDTO) {
        //验证是否符合代金券
        if (addCouponsDTO.getDiscountAmount() == null) {
            throw new BusinessException("减免金额为空");
        }

        verifyValidation(addCouponsDTO);
    }

    //------------------------有效期类型验证---------------------------

    /**
     * 是否符合有效期类型
     *
     * @param addCouponsDTO 券DTO
     */
    private void verifyValidation(AddCouponsDTO addCouponsDTO) {
        switch (addCouponsDTO.getValidityType()) {
            case FIXED_DATE -> verifyFixedDate(addCouponsDTO);
            case DYNAMIC_DATE -> verifyDynamicDate(addCouponsDTO);
            default -> throw new BusinessException("不存在的有效期类型");
        }
    }

    private void verifyFixedDate(AddCouponsDTO addCouponsDTO) {
        if (addCouponsDTO.getValidStartTime() == null
                || addCouponsDTO.getValidEndTime() == null) {
            throw new BusinessException("有效期为空");
        }
    }

    private void verifyDynamicDate(AddCouponsDTO addCouponsDTO) {
        if (addCouponsDTO.getValidStartTime() == null) {
            throw new BusinessException("可领取时间为空");
        }
        if (addCouponsDTO.getDaysAfterClaim() == null) {
            throw new BusinessException("有效期限为空");
        }
    }

}
