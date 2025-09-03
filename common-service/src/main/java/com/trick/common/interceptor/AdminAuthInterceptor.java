package com.trick.common.interceptor;

import com.trick.common.utils.JwtUtil;
import com.trick.common.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;


@Component
public class AdminAuthInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取角色
        String role = request.getHeader("Role");
        String adminId = request.getHeader("X-Admin-Id");
        String adminUsername = request.getHeader("Admin-Username");

        //判断角色是否正确
        try {
            if (!"admin".equals(role)) {
                // 如果Token不是admin，拒绝访问
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //权限不足
                return false;
            }

            //否则放行
            Map<String, Object> context = new HashMap<>();
            context.put("X-Admin-Id", adminId);
            context.put("Admin-Username", adminUsername);
            //存入线程
            ThreadLocalUtil.setContext(context);

            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.remove();
    }
}
