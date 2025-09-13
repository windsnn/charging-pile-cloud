package com.trick.pile.controller.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trick.common.result.Result;
import com.trick.common.utils.ThreadLocalUtil;
import com.trick.pile.model.dto.ChargingDTO;
import com.trick.pile.model.vo.ChargingPileVO;
import com.trick.pile.service.ChargingPileService;
import com.trick.pile.service.ChargingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/piles")
public class ChargingPileController {
    @Autowired
    private ChargingPileService chargingPileService;
    @Autowired
    private ChargingService chargingService;

    //查找附件充电桩
    @GetMapping("/nearby")
    public Result<List<ChargingPileVO>> getNearby(Double latitude, Double longitude) {
        return Result.success(chargingPileService.nearbyByRoadDistance(latitude, longitude));
    }

    //查找充电桩信息
    @GetMapping("/{id}")
    public Result<ChargingPileVO> getById(@PathVariable Integer id) throws JsonProcessingException {
        return Result.success(chargingPileService.getChargingPileById(id));
    }

    //开始充电
    @PostMapping("/start")
    public Result<Map<String, String>> startCharging(@RequestBody ChargingDTO chargingDTO) {
        // 获取UserId
        Integer userId = ThreadLocalUtil.getUserId();

        String orderId = chargingService.startCharging(userId, chargingDTO);

        Map<String, String> map = new HashMap<>();
        map.put("orderId", orderId);

        return Result.success(map);
    }

    //结束充电
    @PostMapping("/stop")
    public Result<Map<String, String>> stopCharging(@RequestBody ChargingDTO chargingDTO) {
        // token获取UserId
        Integer userId = ThreadLocalUtil.getUserId();

        return Result.success(chargingService.stopChargingByUser(userId, chargingDTO));
    }
}
