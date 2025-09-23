package com.trick.logs.mapper;

import com.trick.logs.pojo.OperationLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper {
    @Insert("""
                INSERT INTO operation_log(
                    operator_id, operator_name, operation_module, operation_type, operation_desc,
                    request_uri, request_method, request_params, ip_address, execution_time,
                    status, error_msg, create_time, update_time
                ) VALUES (
                    #{operatorId}, #{operatorName}, #{operationModule}, #{operationType}, #{operationDesc},
                    #{requestUri}, #{requestMethod}, #{requestParams}, #{ipAddress}, #{executionTime},
                    #{status}, #{errorMsg}, #{createTime}, #{updateTime}
                )
            """)
    void insertLog(OperationLog log);
}
