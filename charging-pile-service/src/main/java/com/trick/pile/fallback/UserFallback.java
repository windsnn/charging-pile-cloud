package com.trick.pile.fallback;

import com.trick.common.exception.BusinessException;
import com.trick.common.result.Result;
import com.trick.pile.client.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class UserFallback implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return () -> {
            log.error("调用UserClient失败，原因：{}", cause.getMessage(), cause);
            throw new BusinessException("服务暂不可用");
        };
    }
}
