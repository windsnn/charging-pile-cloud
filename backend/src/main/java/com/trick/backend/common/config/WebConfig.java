package com.trick.backend.common.config;

import com.trick.backend.common.interceptor.AdminAuthInterceptor;
import com.trick.backend.common.interceptor.UserAuthInterceptor;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private AdminAuthInterceptor adminAuthInterceptor;
    @Autowired
    private UserAuthInterceptor userAuthInterceptor;

    @Bean
    //WebSocket配置
    @ConditionalOnWebApplication
    @ConditionalOnClass(ServerEndpoint.class)
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    //拦截器配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册管理员拦截器
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin/**") // 拦截所有 /admin/ 开头的路径
                .excludePathPatterns("/admin/auth/**"); // 放行管理员登录相关接口
        // 注册微信用户拦截器
        registry.addInterceptor(userAuthInterceptor)
                .addPathPatterns("/wx/**") // 拦截所有 /wx/ 开头的路径
                .excludePathPatterns("/wx/auth/**"); // 放行微信用户登录相关接口
    }
}
