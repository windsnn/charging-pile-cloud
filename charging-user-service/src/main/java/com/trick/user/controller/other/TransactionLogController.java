package com.trick.user.controller.other;

import com.trick.common.result.Result;
import com.trick.user.model.pojo.TransactionLog;
import com.trick.user.service.TransactionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionLogController {
    @Autowired
    private TransactionLogService transactionLogService;

    @PostMapping("super/addLog")
    public Result<?> addLog(@RequestBody TransactionLog transactionLog) {
        transactionLogService.addLogT(transactionLog);
        return Result.success();
    }
}
