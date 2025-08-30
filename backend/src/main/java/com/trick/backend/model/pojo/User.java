package com.trick.backend.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Integer id;
    private String openid;
    private String nickname;
    private String avatarUrl;
    private BigDecimal balance;
    private String phone;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
