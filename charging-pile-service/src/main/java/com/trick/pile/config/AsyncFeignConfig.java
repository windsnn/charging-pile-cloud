package com.trick.pile.config;

import com.trick.common.utils.ThreadLocalUtil;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsyncFeignConfig {

    @Bean
    public RequestInterceptor asyncRequestInterceptor() {
        return requestTemplate -> {
            Integer userId = ThreadLocalUtil.getUserId();
            requestTemplate.header("X-User-Id", userId.toString());
            requestTemplate.header("Role", "user");
        };
    }
}
