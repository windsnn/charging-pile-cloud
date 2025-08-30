package com.trick.backend.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargingOrder {

    private Integer id;
    private String orderNo;
    private Integer userId;
    private Integer pileId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private BigDecimal powerConsumed;
    private BigDecimal totalFee;
    private Integer paymentMethod;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
