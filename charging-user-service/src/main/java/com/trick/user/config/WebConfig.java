/*
package com.trick.user.config;

import com.trick.user.interceptor.UserAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private UserAuthInterceptor userAuthInterceptor;

    //拦截器配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册微信用户拦截器
        registry.addInterceptor(userAuthInterceptor)
                .addPathPatterns("/wx/**") // 拦截所有 /wx/ 开头的路径
                .excludePathPatterns("/wx/auth/**"); // 放行微信用户登录相关接口
    }
}
*/
