package com.trick.pile.client;

import com.trick.common.config.FeignConfig;
import com.trick.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "user-wallet-service", configuration = FeignConfig.class)
public interface UserClient {

    //获取当前钱余额
    @GetMapping("wx/wallet")
    Result<Map<String, BigDecimal>> getWallet();
}
