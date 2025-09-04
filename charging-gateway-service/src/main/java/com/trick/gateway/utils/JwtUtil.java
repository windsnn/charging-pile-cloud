package com.trick.gateway.utils;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RefreshScope
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    //解析token
    public Map<String, Object> parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
