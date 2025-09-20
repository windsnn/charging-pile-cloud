package com.trick.marketing.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCoupons {

    private Integer id;
    private Integer userId;
    private Integer couponId;
    private LocalDateTime claimedAt;
    private LocalDateTime expireAt;
    private LocalDateTime usedAt;
    private Integer transactionId;

}
