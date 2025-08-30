package com.trick.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaultReportQueryDTO {

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private String pileNo;//充电桩编号
    private Integer status;
    private Integer userId;

}
