package com.trick.wallet.mapper;


import com.trick.wallet.model.pojo.TransactionLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TransactionLogMapper {
    //获取当前用户全部交易流水
    List<TransactionLog> getAllTransactions(Integer id);

    //插入交易记录
    void addLogT(TransactionLog logT);
}
