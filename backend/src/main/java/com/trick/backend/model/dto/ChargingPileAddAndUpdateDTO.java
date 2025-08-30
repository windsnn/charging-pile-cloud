package com.trick.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargingPileAddAndUpdateDTO {

    private Integer id;
    private String pileNo;
    private Integer type;
    private Integer status;
    private Double powerRate;
    private BigDecimal pricePerKwh;
    private String locationDesc;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
