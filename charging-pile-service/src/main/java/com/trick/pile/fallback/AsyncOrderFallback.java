package com.trick.pile.fallback;

import com.trick.common.exception.BusinessException;
import com.trick.pile.client.AsyncOrderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AsyncOrderFallback implements FallbackFactory<AsyncOrderClient> {
    @Override
    public AsyncOrderClient create(Throwable cause) {
        return map -> {
            log.error("调用AsyncOrderClient失败，原因：{}", cause.getMessage(), cause);
            throw new BusinessException("服务暂不可用");
        };
    }
}
