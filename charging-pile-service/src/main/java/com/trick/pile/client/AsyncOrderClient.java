package com.trick.pile.client;

import com.trick.common.result.Result;
import com.trick.pile.config.AsyncFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "order-service", contextId = "async", configuration = AsyncFeignConfig.class)
public interface AsyncOrderClient {
    //异步结束订单
    @PutMapping("/wx/charging/finalizeCharging")
    Result<Map<String, String>> asyncFinalizeCharging(@RequestBody Map<String, String> map);

}
