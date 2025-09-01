package com.trick.wallet.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

@Mapper
public interface WalletMapper {

    //获取个人钱包余额
    BigDecimal getWallet(Integer id);

    //更新个人钱包余额
    void updateBalance(Integer id, BigDecimal amount);

}
