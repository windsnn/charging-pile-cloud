package com.trick.backend.service.impl;

import com.trick.backend.mapper.OperationLogMapper;
import com.trick.backend.model.pojo.OperationLog;
import com.trick.backend.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl implements OperationLogService {
    @Autowired
    private OperationLogMapper operationLogMapper;

    //添加操作日志
    @Async
    @Override
    public void addOperationLog(OperationLog operationLog) {
        operationLogMapper.addOperationLog(operationLog);
    }
}
