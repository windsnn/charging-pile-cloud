package com.trick.backend.mapper;

import com.trick.backend.model.pojo.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper {
    //异步添加日志
    void addOperationLog(OperationLog operationLog);
}
