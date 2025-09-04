package com.trick.user.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trick.common.exception.BusinessException;
import com.trick.user.model.dto.UserAddAndUpdateDTO;
import com.trick.user.service.LoginService;
import com.trick.user.service.UserService;
import com.trick.user.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    private static final String WX_SESSION_KEY_PREFIX = "wx:sessionkey:";
    @Value("${wx.appid}")
    private String appid;
    @Value("${wx.secret}")
    private String secret;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    /**
     * 微信用户登录
     *
     * @param code 前段获取的微信登录code
     * @return token
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String loginUser(String code) throws JsonProcessingException {
        // 1. 调用微信 API 获取 openid & session_key
        String url = "https://api.weixin.qq.com/sns/jscode2session" +
                "?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";

        String response = restTemplate.getForObject(url, String.class, appid, secret, code);
        log.info("微信登录返回: {}", response);
        log.info("code: {}", code);

        JsonNode jsonNode = objectMapper.readTree(response);

        if (jsonNode.has("errcode")) {
            String errMsg = jsonNode.get("errmsg").asText();
            log.error("微信登录失败: {}", errMsg);
            throw new BusinessException(1001, "微信登录失败");
        }

        String openid = jsonNode.get("openid").asText(null);
        String sessionKey = jsonNode.get("session_key").asText(null);

        if (openid == null || sessionKey == null) {
            throw new BusinessException(1001, "微信登录失败：未获取到openid或session_key");
        }

        // 2. 创建或更新用户，并生成 token
        Integer userId = userService.getUserByOpenid(openid);
        LocalDateTime now = LocalDateTime.now();

        if (userId == null) {
            UserAddAndUpdateDTO dto = new UserAddAndUpdateDTO();
            dto.setOpenid(openid);
            dto.setCreateTime(now);
            dto.setUpdateTime(now);
            userId = userService.addUser(dto);
            log.info("新用户注册成功: openid={}", openid);
        }

        String redisKey = WX_SESSION_KEY_PREFIX + userId;
        stringRedisTemplate.opsForValue().set(redisKey, sessionKey, 7, TimeUnit.DAYS); // 存7天
        log.info("【Redis】已缓存 userId: {} 的 session_key", userId);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("openid", openid);
        claims.put("role", "user");

        return jwtUtil.getToken(claims);
    }
}