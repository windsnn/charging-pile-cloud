package com.trick.order.fallback;

import com.trick.common.exception.BusinessException;
import com.trick.common.result.Result;
import com.trick.order.client.PileClient;
import com.trick.order.model.vo.ChargingPileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PileFallback implements FallbackFactory<PileClient> {
    @Override
    public PileClient create(Throwable cause) {
        return new PileClient() {

            @Override
            public Result<ChargingPileVO> getChargingPile(Integer pileId) {
                log.error("调用getChargingPile失败，原因：{}", cause.getMessage(), cause);
                throw new BusinessException("服务暂不可用");
            }

            @Override
            public Result<?> setState(Integer pileId, Integer state) {
                log.error("调用setState失败，原因：{}", cause.getMessage(), cause);
                throw new BusinessException("服务暂不可用");
            }
        };
    }
}
