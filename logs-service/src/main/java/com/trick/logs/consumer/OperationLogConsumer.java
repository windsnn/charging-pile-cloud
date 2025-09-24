package com.trick.logs.consumer;

import com.trick.logs.mapper.OperationLogMapper;
import com.trick.logs.pojo.OperationLog;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OperationLogConsumer {
    @Autowired
    private OperationLogMapper operationLogMapper;

    /**
     * 监听RabbitMQ消息队列，记录日志
     *
     * @param operationLog 日志
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "operation.logs.queue1"),
            exchange = @Exchange(name = "logs.direct"),
            key = "operation"
    ))
    public void handleOperationLog(OperationLog operationLog) {
        operationLogMapper.insertLog(operationLog);
    }

}
