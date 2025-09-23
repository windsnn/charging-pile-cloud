package com.trick.logs.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfig {
    //将MQ默认消息序列化改为Jackson
    @Bean
    public MessageConverter messageAckListener() {
        return new Jackson2JsonMessageConverter();
    }
}
