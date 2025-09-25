package com.trick.pile.fallback;

import com.trick.common.exception.BusinessException;
import com.trick.common.result.Result;
import com.trick.pile.client.OrderClient;
import com.trick.pile.model.dto.ChargingOrderAddDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class OrderFallback implements FallbackFactory<OrderClient> {

    @Override
    public OrderClient create(Throwable cause) {
        return new OrderClient() {

            @Override
            public Result<?> addOrder(ChargingOrderAddDTO chargingOrderAddDTO) {
                log.error("调用addOrder失败，原因：{}", cause.getMessage(), cause);
                throw new BusinessException("服务暂不可用");
            }

            @Override
            public Result<Map<String, String>> finalizeCharging(Map<String, String> map) {
                log.error("调用finalizeCharging失败，原因：{}", cause.getMessage(), cause);
                throw new BusinessException("服务暂不可用");
            }
        };
    }
}
