package com.trick.wallet.service;

import com.trick.common.result.PageResult;
import com.trick.wallet.model.pojo.TransactionLog;

public interface TransactionLogService {
    PageResult<TransactionLog> getPagedTransactions(Integer id, Integer pageNum, Integer pageSize);

    //插入交易流水号 1.钱包充值，2.钱包消费
    void addLogTransactions(Integer userId, TransactionLog transactionLog);
}
