package com.trick.order.controller;

import com.trick.common.result.Result;
import com.trick.common.utils.ThreadLocalUtil;
import com.trick.order.model.vo.ChargingOrderVO;
import com.trick.order.service.ChargingOrderService;
import com.trick.order.service.ChargingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/charging")
public class ChargingController {
    @Autowired
    private ChargingOrderService chargingOrderService;
    @Autowired
    private ChargingService chargingService;

    @GetMapping("/ongoing")
    public Result<List<ChargingOrderVO>> ongoingCharging() {
        // token获取UserId
        Integer userId = (Integer) ThreadLocalUtil.getContext().get("id");

        return Result.success(chargingOrderService.getOngoing(userId));
    }

    @PutMapping("/finalizeCharging")
    public Result<Map<String, String>> finalizeCharging(@RequestBody Map<String, String> map) {
        Integer userId = (Integer) ThreadLocalUtil.getContext().get("id");

        HashMap<String, String> hashMap = new HashMap<>();
        String orderNo = chargingService.finalizeCharging(userId, map.get("orderNo"));
        hashMap.put("orderNo", orderNo);

        return Result.success(hashMap);
    }
}
