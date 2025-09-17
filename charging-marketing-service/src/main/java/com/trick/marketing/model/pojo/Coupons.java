package com.trick.marketing.model.pojo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupons {

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
