package com.trick.pile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {

    /**
     * 这个 Bean 会自动注册使用了 @ServerEndpoint 注解的类
     * 只有在使用 Spring Boot 内嵌容器时才需要
     * 如果部署到外部 Tomcat/Jetty，就不需要这个 Bean
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
