package com.trick.pile.client;

import com.trick.common.config.FeignConfig;
import com.trick.common.result.Result;
import com.trick.pile.fallback.OrderFallback;
import com.trick.pile.model.dto.ChargingOrderAddDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "order-service", configuration = FeignConfig.class,fallbackFactory = OrderFallback.class)
public interface OrderClient {

    //添加订单
    @PostMapping("/wx/orders")
    Result<?> addOrder(@RequestBody ChargingOrderAddDTO chargingOrderAddDTO);

    //结束订单
    @PutMapping("/wx/charging/finalizeCharging")
    Result<Map<String,String>> finalizeCharging(@RequestBody Map<String,String> map);

}
