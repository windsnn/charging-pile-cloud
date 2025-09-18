package com.trick.marketing.model.vo.wxCouponsVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponBaseVO {
    private Integer status;//用户券状态
    private LocalDateTime claimedAt;//领取时间
    private LocalDateTime expireAt;//过期时间
    private LocalDateTime usedAt;//使用时间
    private Integer transactionId;//交易号ID

    private String name;//券名称
    private Integer type;//券类型
    private String description;//券描述
    private BigDecimal minSpend;//最低消费金额

}
