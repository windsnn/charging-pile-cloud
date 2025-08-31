package com.trick.order.controller;

import com.trick.common.result.Result;
import com.trick.common.utils.ThreadLocalUtil;
import com.trick.order.model.vo.ChargingOrderVO;
import com.trick.order.service.ChargingOrderService;
import com.trick.order.service.ChargingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wx/charging")
public class ChargingController {
    @Autowired
    private ChargingService chargingService;
    @Autowired
    private ChargingOrderService chargingOrderService;

    @GetMapping("/ongoing")
    public Result<List<ChargingOrderVO>> ongoingCharging() {
        // token获取UserId
        Integer userId = (Integer) ThreadLocalUtil.getContext().get("id");

        return Result.success(chargingOrderService.getOngoing(userId));
    }
}
