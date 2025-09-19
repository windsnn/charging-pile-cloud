package com.trick.wallet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmountDTO {
    Integer couponId;//优惠券id

    BigDecimal amount;
}
