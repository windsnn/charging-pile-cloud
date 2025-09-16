package com.trick.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.trick.common.result.PageResult;
import com.trick.user.mapper.TransactionLogMapper;
import com.trick.user.model.pojo.TransactionLog;
import com.trick.user.service.TransactionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionLogServiceImpl implements TransactionLogService {
    @Autowired
    private TransactionLogMapper transactionLogMapper;

    //分页查询交易记录
    @Override
    public PageResult<TransactionLog> getPagedTransactions(Integer id, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TransactionLog> list = transactionLogMapper.getAllTransactions(id);
        PageInfo<TransactionLog> pageInfo = new PageInfo<>(list);

        List<TransactionLog> records = pageInfo.getList();
        long total = pageInfo.getTotal();

        return new PageResult<>(total, records);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addLogT(TransactionLog logT) {
        transactionLogMapper.addLogT(logT);
    }

}
