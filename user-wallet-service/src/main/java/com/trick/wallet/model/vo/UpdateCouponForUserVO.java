package com.trick.wallet.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCouponForUserVO {
    LocalDateTime usedAt;
    Integer transactionId;
}
