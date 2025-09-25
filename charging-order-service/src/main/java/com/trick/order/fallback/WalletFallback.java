package com.trick.order.fallback;

import com.trick.common.exception.BusinessException;
import com.trick.common.result.Result;
import com.trick.order.client.WalletClient;
import com.trick.order.model.dto.AmountDTO;
import com.trick.order.model.pojo.TransactionLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@Slf4j
public class WalletFallback implements FallbackFactory<WalletClient> {

    @Override
    public WalletClient create(Throwable cause) {
        return new WalletClient() {
            @Override
            public Result<Map<String, BigDecimal>> getWallet() {
                log.error("调用getWallet失败，原因：{}", cause.getMessage(), cause);
                throw new BusinessException("服务暂不可用");
            }

            @Override
            public Result<?> deductAmount(AmountDTO amountDTO) {
                log.error("调用deductAmount失败，原因：{}", cause.getMessage(), cause);
                throw new BusinessException("服务暂不可用");
            }

            @Override
            public Result<?> addLogT(TransactionLog transactionLog) {
                log.error("调用addLogT失败，原因：{}", cause.getMessage(), cause);
                throw new BusinessException("服务暂不可用");
            }
        };
    }
}
