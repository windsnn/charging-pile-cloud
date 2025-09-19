package com.trick.wallet.service;

import com.trick.wallet.model.dto.AmountDTO;

import java.math.BigDecimal;

public interface WalletService {
    BigDecimal getWallet(Integer userId);

    void deductAmount(Integer id, BigDecimal amount);

    //充值模块（钱包充值）
    //目前为模拟充值
    void recharge(Integer userId, AmountDTO amountDTO);

}
