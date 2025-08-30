package com.trick.backend.service;

import com.trick.backend.common.result.PageResult;
import com.trick.backend.model.pojo.TransactionLog;

import java.math.BigDecimal;

public interface TransactionLogService {

    PageResult<TransactionLog> getPagedTransactions(Integer id, Integer pageNum, Integer pageSize);

    void recharge(Integer id, BigDecimal amount);

}
