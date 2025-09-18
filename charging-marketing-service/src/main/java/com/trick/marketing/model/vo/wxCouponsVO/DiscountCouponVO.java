package com.trick.marketing.model.vo.wxCouponsVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountCouponVO extends CouponBaseVO {
    private BigDecimal discountPercent;//折扣百分比
    private BigDecimal maxDiscountAmount;//最高折扣金额
}