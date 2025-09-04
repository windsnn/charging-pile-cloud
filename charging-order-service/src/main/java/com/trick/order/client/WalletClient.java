package com.trick.order.client;

import com.trick.common.config.FeignConfig;
import com.trick.common.result.Result;
import com.trick.order.model.dto.AmountDTO;
import com.trick.order.model.pojo.TransactionLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "user-wallet-service", configuration = FeignConfig.class)
public interface WalletClient {

    @GetMapping("/wx/wallet")
    Result<Map<String, BigDecimal>> getWallet();

    @PostMapping("wx/wallet/deduction")
    Result<?> deductAmount(AmountDTO amountDTO);

    @PostMapping("wx/wallet/transactions")
    Result<?> addLogT(TransactionLog log);
}
