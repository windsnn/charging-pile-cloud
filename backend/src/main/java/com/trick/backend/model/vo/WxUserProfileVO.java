package com.trick.backend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxUserProfileVO {

    private String nickname;
    private String avatarUrl;
    private BigDecimal balance;
    private String phone;

}
