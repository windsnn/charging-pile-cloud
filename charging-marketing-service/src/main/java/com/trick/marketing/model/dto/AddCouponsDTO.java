package com.trick.marketing.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCouponsDTO {

    private String code;
    private String name;
    private Integer type;
    private String description;
    private Integer totalQuantity;
    private Integer validityType;
    private LocalDateTime validStartTime;
    private LocalDateTime validEndTime;
    private Integer daysAfterClaim;
    private Double discountPercent;
    private Double minSpend;
    private Double maxDiscountAmount;
    private Double discountAmount;

}
