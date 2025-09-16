package com.trick.pile.fallback;

import com.trick.common.result.Result;
import com.trick.pile.client.OrderClient;
import com.trick.pile.model.dto.ChargingOrderAddDTO;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderFallback implements FallbackFactory<OrderClient> {

    @Override
    public OrderClient create(Throwable cause) {
        return new OrderClient() {

            @Override
            public Result<?> addOrder(ChargingOrderAddDTO chargingOrderAddDTO) {
                return Result.error("添加订单失败");
            }

            @Override
            public Result<Map<String, String>> finalizeCharging(Map<String, String> map) {
                return Result.error("结束充电失败");
            }
        };
    }
}
