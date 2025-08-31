package com.trick.user.mapper;

import com.trick.user.model.pojo.TransactionLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TransactionLogMapper {
    List<TransactionLog> getAllTransactions(Integer id);

    //插入充值记录
    void addLogT(TransactionLog logT);
}
