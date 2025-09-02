package com.trick.pile.controller.other;

import com.trick.common.result.Result;
import com.trick.pile.model.dto.ChargingPileAddAndUpdateDTO;
import com.trick.pile.service.ChargingPileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/super")
public class PileStateController {
    @Autowired
    private ChargingPileService chargingPileService;

    //内部设置空闲接口
    @PutMapping("/state")
    public Result<?> SetState(Integer id, Integer state) {
        ChargingPileAddAndUpdateDTO dto = new ChargingPileAddAndUpdateDTO();
        dto.setStatus(state);
        chargingPileService.updateChargingPile(id, dto);
        return Result.success();
    }
}
