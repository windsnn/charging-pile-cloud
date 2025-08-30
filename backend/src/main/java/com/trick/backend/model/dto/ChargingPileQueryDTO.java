package com.trick.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargingPileQueryDTO {

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private String pileNo;//编号
    private Integer status;//状态
    private String locationDesc;//地点

}
