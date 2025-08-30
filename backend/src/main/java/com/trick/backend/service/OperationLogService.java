package com.trick.backend.service;

import com.trick.backend.model.pojo.OperationLog;

public interface OperationLogService {
    void addOperationLog(OperationLog operationLog);
}
