package com.trick.gateway.filters;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.trick.gateway.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Order(-1)
public class MyGlobalFilter implements GlobalFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1 获取请求头内容token和uri
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        List<String> token = headers.get("token");
        String path = request.getURI().getPath();

        //判断请求路径是否为/wx/auth/login 或者 /admin/auth/login 是则放行
        if ("/wx/auth/login".equals(path) || "/admin/auth/login".equals(path)) {
            return chain.filter(exchange);
        }

        // 如果token为空，直接返回权限不足
        if (CollectionUtils.isEmpty(token)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED); //权限不足，返回401
            return response.setComplete();
        }

        //2 解析请求头的token信息
        Map<String, Object> parseToken;
        try {
            parseToken = jwtUtil.parseToken(token.get(0));
        } catch (Exception e) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED); //权限不足，返回401
            return response.setComplete();
        }

        //3 转化上下文发送给下游
        ServerWebExchange newExchange = exchange.mutate()
                .request(builder -> {
                    builder.header("X-User-Id", Objects.toString(parseToken.get("userId"), ""));
                    builder.header("X-Admin-Id", Objects.toString(parseToken.get("adminId"), ""));
                    builder.header("Admin-Username", Objects.toString(parseToken.get("username"), ""));
                    builder.header("Role", Objects.toString(parseToken.get("role"), ""));
                    builder.header("Openid", Objects.toString(parseToken.get("openid"), ""));
                })
                .build();

        return chain.filter(newExchange);
    }
}