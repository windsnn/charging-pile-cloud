package com.trick.marketing.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponConsumerDTO {
    private Integer userId;
    private Integer couponId;
    private Integer validityType;
    private LocalDateTime validEndTime;
    private Integer daysAfterClaim;
}
