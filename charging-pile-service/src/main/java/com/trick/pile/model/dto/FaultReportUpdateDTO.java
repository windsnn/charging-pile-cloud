package com.trick.pile.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaultReportUpdateDTO {

    private Integer id;
    private Integer status;
    private LocalDateTime updateTime;

}
