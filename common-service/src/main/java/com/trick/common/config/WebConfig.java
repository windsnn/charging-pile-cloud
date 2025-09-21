package com.trick.common.config;

import com.trick.common.interceptor.AdminAuthInterceptor;
import com.trick.common.interceptor.InternalAuthInterceptor;
import com.trick.common.interceptor.UserAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private AdminAuthInterceptor adminAuthInterceptor;
    @Autowired
    private UserAuthInterceptor userAuthInterceptor;
    @Autowired
    private InternalAuthInterceptor internalAuthInterceptor;

    //拦截器配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册管理员拦截器
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin/**")// 拦截所有 /admin/ 开头的路径
                .excludePathPatterns("/admin/auth/**"); // 放行微信用户登录相关接口
        // 注册微信用户拦截器
        registry.addInterceptor(userAuthInterceptor)
                .addPathPatterns("/wx/**") // 拦截所有 /wx/ 开头的路径
                .excludePathPatterns("/wx/auth/**"); // 放行微信用户登录相关接口
        //注册内部接口拦截器
        registry.addInterceptor(internalAuthInterceptor)
                .addPathPatterns("/internal/**");//拦截所有内部接口
    }
}
