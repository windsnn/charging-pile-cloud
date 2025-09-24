package com.trick.logs.service;

import com.trick.logs.pojo.OperationLog;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到RabbitMQ
     * 创建并绑定队列交换机
     *
     * @param operationLog 发送的消息，操作日志实体
     */
    public void sendOperationLog(OperationLog operationLog) {
        try {
            rabbitTemplate.convertAndSend("logs.direct", "operation", operationLog);
        } catch (AmqpException e) {
            throw new RuntimeException("消息队列发送出错", e);
        }
    }

}