package com.trick.backend.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionLog {

    private Integer id;
    private String transactionNo;
    private Integer userId;
    private Integer orderId;
    private BigDecimal amount;
    private Integer type;
    private Integer status;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
