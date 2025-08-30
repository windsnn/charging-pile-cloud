package com.trick.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.trick.backend.common.exception.BusinessException;
import com.trick.backend.common.result.PageResult;
import com.trick.backend.mapper.TransactionLogMapper;
import com.trick.backend.model.pojo.TransactionLog;
import com.trick.backend.service.TransactionLogService;
import com.trick.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionLogServiceImpl implements TransactionLogService {
    @Autowired
    private TransactionLogMapper transactionLogMapper;
    @Autowired
    private UserService userService;

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

    //充值模块（钱包充值）
    //目前为模拟充值
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recharge(Integer id, BigDecimal amount) {
        // 1. 校验参数
        if (id == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(422, "充值金额必须大于0");
        }

        // 2. 更新用户钱包余额
        userService.addBalance(id, amount);

        // 3. 生成交易流水号
        String transactionNo = "TX" + System.currentTimeMillis() + (int) (Math.random() * 1000);

        // 4. 插入交易记录
        TransactionLog logT = new TransactionLog();
        logT.setTransactionNo(transactionNo);
        logT.setUserId(id);
        logT.setOrderId(null);
        logT.setAmount(amount);
        logT.setType(1); // 1-充值
        logT.setStatus(1); // 1-成功
        logT.setDescription("模拟充值");
        transactionLogMapper.addLogT(logT);
    }

}
