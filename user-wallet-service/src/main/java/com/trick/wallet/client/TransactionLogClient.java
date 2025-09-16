package com.trick.wallet.client;

import com.trick.common.config.FeignConfig;
import com.trick.common.result.Result;
import com.trick.wallet.model.pojo.TransactionLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface TransactionLogClient {

    @PostMapping("/super/addLog")
    Result<?> addLogT(@RequestBody TransactionLog transactionLog);
}
