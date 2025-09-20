package com.trick.marketing.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCouponsDTO {

    private String code;
    private String name;
    private Integer type;
    private String description;
    private Integer status;
    private Integer isHot;
    private Integer totalQuantity;
    private Integer validityType;
    private LocalDateTime validStartTime;
    private LocalDateTime validEndTime;
    private Integer daysAfterClaim;
    private BigDecimal discountPercent;
    private BigDecimal minSpend;
    private BigDecimal maxDiscountAmount;
    private BigDecimal discountAmount;

}
