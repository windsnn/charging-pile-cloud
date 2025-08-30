package com.trick.backend.controller.wx;

import com.trick.backend.common.result.Result;
import com.trick.backend.common.utils.ThreadLocalUtil;
import com.trick.backend.model.dto.ChargingDTO;
import com.trick.backend.model.vo.ChargingOrderVO;
import com.trick.backend.service.ChargingOrderService;
import com.trick.backend.service.ChargingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/charging")
public class WxChargingController {
    @Autowired
    private ChargingService chargingService;
    @Autowired
    private ChargingOrderService chargingOrderService;


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

    @GetMapping("/ongoing")
    public Result<List<ChargingOrderVO>> ongoingCharging() {
        // token获取UserId
        Integer userId = (Integer) ThreadLocalUtil.getContext().get("id");

        return Result.success(chargingOrderService.getOngoing(userId));
    }
}
