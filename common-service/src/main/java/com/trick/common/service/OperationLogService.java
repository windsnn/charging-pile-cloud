package com.trick.common.service;

import com.trick.common.pojo.OperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OperationLogService {
/*
    @Autowired
    private OperationLogMapper operationLogMapper;
*/

    //添加操作日志
    @Async
    public void addOperationLog(OperationLog operationLog) {
        //todo MQ异步存储日志
    }
}