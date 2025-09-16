package com.trick.wallet.service;

import java.math.BigDecimal;

public interface WalletService {
    BigDecimal getWallet(Integer userId);

    void deductAmount(Integer id, BigDecimal amount);

    //充值模块（钱包充值）
    //目前为模拟充值
    void recharge(Integer id, BigDecimal amount);

}
