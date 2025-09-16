package com.trick.pile.fallback;

import com.trick.common.result.Result;
import com.trick.pile.client.UserClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class UserFallback implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return () -> Result.error("获取余额失败，稍后重试", Collections.emptyMap());
    }
}
