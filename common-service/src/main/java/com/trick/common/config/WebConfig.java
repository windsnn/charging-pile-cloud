package com.trick.common.config;

import com.trick.common.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private AuthInterceptor authInterceptor;

    //拦截器配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        registry.addInterceptor(authInterceptor)
                /*.addPathPatterns("/**")
                .excludePathPatterns("/wx/auth/login")
                .excludePathPatterns("/admin/auth/login"); // 拦截所有路径,放行登录*/
                .excludePathPatterns("/**");
    }
}