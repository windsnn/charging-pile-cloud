package com.trick.pile.fallback;

import com.trick.common.result.Result;
import com.trick.pile.client.AsyncOrderClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class AsyncOrderFallback implements FallbackFactory<AsyncOrderClient> {
    @Override
    public AsyncOrderClient create(Throwable cause) {
        return map -> Result.error("启动充电失败，请稍后重试");
    }
}
