package com.trick.user.controller.wechat;

import com.trick.common.result.PageResult;
import com.trick.common.result.Result;
import com.trick.user.model.dto.RechargeDTO;
import com.trick.user.model.pojo.TransactionLog;
import com.trick.user.service.TransactionLogService;
import com.trick.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.trick.common.utils.ThreadLocalUtil;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wx/wallet")
public class WalletController {
    @Autowired
    private TransactionLogService transactionLogService;
    @Autowired
    private UserService userService;

    @GetMapping
    public Result<Map<String, BigDecimal>> getWallet() {
        // token获取id
        Integer userId = (Integer) ThreadLocalUtil.getContext().get("id");
        BigDecimal balance = userService.getWallet(userId);

        Map<String, BigDecimal> map = new HashMap<>();
        map.put("balance", balance);

        return Result.success(map);
    }

    @GetMapping("/transactions")
    public Result<PageResult<TransactionLog>> getPagedTransactions(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        // token获取id
        Integer userId = (Integer) ThreadLocalUtil.getContext().get("id");

        return Result.success(transactionLogService.getPagedTransactions(userId, pageNum, pageSize));
    }

    @PostMapping("/recharge")
    public Result<?> recharge(@RequestBody RechargeDTO rechargeDTO) {
        // token获取id
        Integer userId = (Integer) ThreadLocalUtil.getContext().get("id");

        transactionLogService.recharge(userId, rechargeDTO.getAmount());
        return Result.success();
    }

}
