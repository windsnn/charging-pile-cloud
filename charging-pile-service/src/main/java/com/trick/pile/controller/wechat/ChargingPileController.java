package com.trick.pile.controller.wechat;

import com.trick.common.result.Result;
import com.trick.pile.model.vo.ChargingPileVO;
import com.trick.pile.service.ChargingPileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wx/piles")
public class ChargingPileController {
    @Autowired
    private ChargingPileService chargingPileService;

    @GetMapping("/nearby")
    public Result<List<ChargingPileVO>> getNearby(Double latitude, Double longitude) {
        return Result.success(chargingPileService.nearbyByRoadDistance(latitude, longitude));
    }

    @GetMapping("/{id}")
    public Result<ChargingPileVO> getById(@PathVariable Integer id) {
        return Result.success(chargingPileService.getChargingPileById(id));
    }
}
