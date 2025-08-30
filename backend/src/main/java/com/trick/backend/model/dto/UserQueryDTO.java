package com.trick.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private String nickname;
    private String phone;
}
