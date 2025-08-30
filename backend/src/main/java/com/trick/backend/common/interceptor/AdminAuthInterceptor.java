package com.trick.backend.common.interceptor;

import com.trick.backend.common.utils.JwtUtil;
import com.trick.backend.common.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取令牌
        String token = request.getHeader("token");
        System.out.println(token);

        //判断令牌是否存在
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        //判断令牌是否有效
        try {
            Map<String, Object> parseToken = jwtUtil.parseToken(token);
            String role = (String) parseToken.get("role");
            System.out.println(role);
            if (!"admin".equals(role)) {
                // 如果Token不是admin，拒绝访问
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }

            ThreadLocalUtil.setContext(parseToken);
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
