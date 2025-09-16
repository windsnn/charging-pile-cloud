package com.trick.user.service;


import com.trick.common.result.PageResult;
import com.trick.user.model.pojo.TransactionLog;

import java.math.BigDecimal;

public interface TransactionLogService {

    PageResult<TransactionLog> getPagedTransactions(Integer id, Integer pageNum, Integer pageSize);

    //添加记录
    void addLogT(TransactionLog logT);
}
