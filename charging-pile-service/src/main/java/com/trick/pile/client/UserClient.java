package com.trick.pile.client;

import com.trick.common.result.Result;
import com.trick.pile.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserClient {

    //获取当前钱余额
    @GetMapping("wx/wallet")
    Result<Map<String, BigDecimal>> getWallet();
}
