package com.trick.backend.mapper;

import com.trick.backend.model.pojo.TransactionLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TransactionLogMapper {
    List<TransactionLog> getAllTransactions(Integer id);

    //插入充值记录
    void addLogT(TransactionLog logT);
}
