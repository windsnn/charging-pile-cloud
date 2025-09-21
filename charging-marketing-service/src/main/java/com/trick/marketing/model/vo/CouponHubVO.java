package com.trick.marketing.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponHubVO {
    private Integer id;
    private String name;
    private Integer type;
    private String description;
    private Integer totalQuantity;
    private Integer claimedQuantity;

    private Integer validityType;
    private LocalDateTime validStartTime;
    private LocalDateTime validEndTime;
    private Integer daysAfterClaim;
    private Double discountPercent;
    private Double minSpend;
    private Double maxDiscountAmount;
    private Double discountAmount;

}
