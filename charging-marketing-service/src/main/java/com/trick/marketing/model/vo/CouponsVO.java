package com.trick.marketing.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponsVO {
    private Integer id;
    private String code;
    private String name;
    private Integer type;
    private String description;
    private Integer status;
    private Integer totalQuantity;
    private Integer claimedQuantity;
    private Integer usedQuantity;
    private Integer validityType;
    private LocalDateTime validStartTime;
    private LocalDateTime validEndTime;
    private Integer daysAfterClaim;
    private Double discountPercent;
    private Double minSpend;
    private Double maxDiscountAmount;
    private Double discountAmount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
