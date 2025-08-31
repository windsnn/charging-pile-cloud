package com.trick.pile.config;

import com.trick.common.utils.ThreadLocalUtil;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String userId = (String) ThreadLocalUtil.getContext().get("userId");
            requestTemplate.header("userId", userId);
        };
    }
}
