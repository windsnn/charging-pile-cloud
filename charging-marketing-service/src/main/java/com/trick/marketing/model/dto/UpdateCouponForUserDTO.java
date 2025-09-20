package com.trick.marketing.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCouponForUserDTO {
    LocalDateTime usedAt;
    Integer transactionId;
}
