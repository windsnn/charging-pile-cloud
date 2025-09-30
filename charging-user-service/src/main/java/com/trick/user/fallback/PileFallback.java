package com.trick.user.fallback;

import com.trick.common.exception.BusinessException;
import com.trick.user.client.PileClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PileFallback implements FallbackFactory<PileClient> {
    @Override
    public PileClient create(Throwable cause) {
        return pileId -> {
            log.error("调用getChargingPileById失败，原因：{}", cause.getMessage(), cause);
            throw new BusinessException("服务暂不可用");
        };
    }
}
