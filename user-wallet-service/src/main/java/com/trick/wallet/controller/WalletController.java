package com.trick.wallet.controller;

import com.trick.common.result.PageResult;
import com.trick.common.result.Result;
import com.trick.common.utils.ThreadLocalUtil;
import com.trick.wallet.model.dto.AmountDTO;
import com.trick.wallet.model.pojo.TransactionLog;
import com.trick.wallet.service.TransactionLogService;
import com.trick.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wx/wallet")
public class WalletController {
    @Autowired
    private TransactionLogService transactionLogService;
    @Autowired
    private WalletService walletService;

    @GetMapping
    //获取钱包余额
    public Result<Map<String, BigDecimal>> getWallet() {
        // 获取userId
        Integer userId = ThreadLocalUtil.getUserId();
        BigDecimal balance = walletService.getWallet(userId);

        Map<String, BigDecimal> map = new HashMap<>();
        map.put("balance", balance);

        return Result.success(map);
    }

    //钱包充值
    @PostMapping("/recharge")
    public Result<?> recharge(@RequestBody AmountDTO amountDTO) {
        // 获取userId
        Integer userId = ThreadLocalUtil.getUserId();

        walletService.recharge(userId, amountDTO.getAmount());
        return Result.success();
    }

    //钱包扣款
    @PostMapping("/deduction")
    public Result<?> deductAmount(@RequestBody AmountDTO amountDTO) {
        Integer userId = ThreadLocalUtil.getUserId();

        walletService.deductAmount(userId, amountDTO.getAmount());
        return Result.success();
    }

    //获取交易流水表
    @GetMapping("/transactions")
    public Result<PageResult<TransactionLog>> getPagedTransactions(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        // 获取userId
        Integer userId = ThreadLocalUtil.getUserId();

        return Result.success(transactionLogService.getPagedTransactions(userId, pageNum, pageSize));
    }

    //添加交易流水表
    @PostMapping("/transactions")
    public Result<?> addTransaction(@RequestBody TransactionLog transactionLog) {
        Integer userId = ThreadLocalUtil.getUserId();
        transactionLog.setUserId(userId);

        transactionLogService.addLogTransactions(userId, transactionLog);
        return Result.success();
    }

}
