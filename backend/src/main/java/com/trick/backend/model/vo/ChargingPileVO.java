package com.trick.backend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargingPileVO {

    private Integer id;
    private String pileNo;
    private Integer type;
    private Integer status;
    private BigDecimal powerRate;
    private BigDecimal pricePerKwh;
    private String locationDesc;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Double roadDistance; //道路距离

}
