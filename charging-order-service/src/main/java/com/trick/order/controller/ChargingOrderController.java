package com.trick.order.controller;

import com.trick.common.result.PageResult;
import com.trick.common.result.Result;
import com.trick.common.utils.ThreadLocalUtil;
import com.trick.order.model.dto.ChargingOrderAddDTO;
import com.trick.order.model.vo.ChargingOrderVO;
import com.trick.order.service.ChargingOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wx/orders")
public class ChargingOrderController {
    @Autowired
    private ChargingOrderService orderService;
    @Autowired
    private ChargingOrderService chargingOrderService;

    //分页获取我的订单(倒序排序）
    @GetMapping()
    public Result<PageResult<ChargingOrderVO>> getUserProfileOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        //获取 userId
        Integer userId = ThreadLocalUtil.getUserId();

        return Result.success(orderService.getPagedOrder(userId, pageNum, pageSize));
    }

    //添加订单
    @PostMapping()
    public Result<?> addOrder(@RequestBody ChargingOrderAddDTO chargingOrderAddDTO) {
        Integer userId = ThreadLocalUtil.getUserId();
        chargingOrderAddDTO.setUserId(userId);
        orderService.addOrder(chargingOrderAddDTO);
        return Result.success();
    }

    @GetMapping("/ongoing")
    public Result<List<ChargingOrderVO>> ongoingCharging() {
        // 获取UserId
        Integer userId = ThreadLocalUtil.getUserId();

        return Result.success(chargingOrderService.getOngoing(userId));
    }
}
