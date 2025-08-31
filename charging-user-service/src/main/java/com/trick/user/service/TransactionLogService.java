package com.trick.user.service;


import com.trick.common.result.PageResult;
import com.trick.user.model.pojo.TransactionLog;

import java.math.BigDecimal;

public interface TransactionLogService {

    PageResult<TransactionLog> getPagedTransactions(Integer id, Integer pageNum, Integer pageSize);

    void recharge(Integer id, BigDecimal amount);

}
