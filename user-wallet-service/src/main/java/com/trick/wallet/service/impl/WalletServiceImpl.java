package com.trick.wallet.service.impl;

import com.trick.common.exception.BusinessException;
import com.trick.common.result.Result;
import com.trick.wallet.client.TransactionLogClient;
import com.trick.wallet.mapper.WalletMapper;
import com.trick.wallet.model.dto.AmountDTO;
import com.trick.wallet.model.pojo.TransactionLog;
import com.trick.wallet.service.TransactionLogService;
import com.trick.wallet.service.WalletService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class WalletServiceImpl implements WalletService {
    @Autowired
    private TransactionLogService transactionLogService;
    @Autowired
    private WalletMapper walletMapper;
    @Autowired
    private TransactionLogClient transactionLogClient;

    //获取个人余额
    @Override
    public BigDecimal getWallet(Integer id) {
        return walletMapper.getWallet(id);
    }

    //账户余额扣款
    @Override
    public void deductAmount(Integer id, BigDecimal amount) {
        walletMapper.updateBalance(id, amount);
    }

    //充值模块（钱包充值）
    //目前为模拟充值
    @Override
    @GlobalTransactional
    public void recharge(Integer id, BigDecimal amount) {
        // 1. 校验参数
        if (id == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(422, "充值金额必须大于0");
        }

        // 2. 生成交易流水号
        String transactionNo = "TX" + System.currentTimeMillis() + (int) (Math.random() * 1000);

        // 3. 插入交易记录
        TransactionLog logT = new TransactionLog();
        logT.setTransactionNo(transactionNo);
        logT.setUserId(id);
        logT.setOrderId(null);
        logT.setAmount(amount);
        logT.setType(1); // 1-充值
        logT.setStatus(0); // 1-成功 0-处理中
        logT.setDescription("模拟充值");

        // 4. 更新用户钱包余额
        try {
            walletMapper.updateBalance(id, amount);
        } catch (Exception e) {
            logT.setStatus(2);//失败
            logT.setDescription("模拟充值失败");
            transactionLogClient.addLogT(logT);

            log.error(e.getMessage(), e);
            throw new BusinessException("添加余额失败");
        }

        //添加充值记录
        logT.setStatus(1);//成功
        transactionLogClient.addLogT(logT);
    }
}
