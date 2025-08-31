package com.trick.pile.controller.wechat;

import com.trick.common.result.Result;
import com.trick.common.utils.ThreadLocalUtil;
import com.trick.pile.model.dto.ChargingDTO;
import com.trick.pile.service.ChargingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wx/charging")
public class ChargingController {
    @Autowired
    private ChargingService chargingService;

    @PostMapping("/start")
    public Result<Map<String, String>> startCharging(@RequestBody ChargingDTO chargingDTO) {
        // token获取UserId
        Integer userId = (Integer) ThreadLocalUtil.getContext().get("id");

        String orderId = chargingService.startCharging(userId, chargingDTO);

        Map<String, String> map = new HashMap<>();
        map.put("orderId", orderId);

        return Result.success(map);
    }

    @PostMapping("/stop")
    public Result<Map<String, String>> stopCharging(@RequestBody ChargingDTO chargingDTO) {
        // token获取UserId
        Integer userId = (Integer) ThreadLocalUtil.getContext().get("id");

        String orderId = chargingService.stopChargingByUser(userId, chargingDTO);

        Map<String, String> map = new HashMap<>();
        map.put("orderId", orderId);

        return Result.success(map);
    }
}
