package com.trick.backend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargingOrderVO {

    private Integer id;
    private String orderNo;
    private Integer pileId;
    private LocalDateTime startTime;
    private Integer status;

    private LocalDateTime endTime;
    private Integer duration;
    private BigDecimal powerConsumed;
    private BigDecimal totalFee;
    private Integer paymentMethod;

}
