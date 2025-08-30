package com.trick.backend.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationLog {

    private Integer id;
    private Integer operatorId;
    private String operatorName;
    private String operationModule;
    private String operationType;
    private String operationDesc;
    private String requestUri;
    private String requestMethod;
    private String requestParams;
    private String ipAddress;
    private Integer executionTime;
    private Integer status;
    private String errorMsg;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
