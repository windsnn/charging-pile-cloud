package com.trick.order.controller;

import com.trick.common.result.PageResult;
import com.trick.common.result.Result;
import com.trick.common.utils.ThreadLocalUtil;
import com.trick.order.model.vo.ChargingOrderVO;
import com.trick.order.service.ChargingOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wx")
public class ChargingOrderController {
    @Autowired
    private ChargingOrderService orderService;

    //分页获取我的订单(倒序排序）
    @GetMapping("/orders")
    public Result<PageResult<ChargingOrderVO>> getUserProfileOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        //获取token id
        Integer userId = (Integer) ThreadLocalUtil.getContext().get("id");

        return Result.success(orderService.getPagedOrder(userId, pageNum, pageSize));
    }
}
