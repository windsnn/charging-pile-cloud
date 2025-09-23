package com.trick.pile.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trick.logs.annotation.LogRecord;
import com.trick.common.result.PageResult;
import com.trick.common.result.Result;
import com.trick.pile.model.dto.ChargingPileAddAndUpdateDTO;
import com.trick.pile.model.dto.ChargingPileQueryDTO;
import com.trick.pile.model.pojo.ChargingPile;
import com.trick.pile.model.vo.ChargingPileVO;
import com.trick.pile.service.ChargingPileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/piles")
public class AdminChargingPileController {
    @Autowired
    private ChargingPileService chargingPileService;

    @GetMapping()
    Result<PageResult<ChargingPile>> getChargingPilesByPage(ChargingPileQueryDTO queryDTO) {
        return Result.success(chargingPileService.getChargingPilesByPage(queryDTO));
    }

    @GetMapping("/{id}")
    Result<ChargingPileVO> getChargingPileById(@PathVariable Integer id) throws JsonProcessingException {
        return Result.success(chargingPileService.getChargingPileById(id));
    }

    @LogRecord(
            module = "充电桩管理",
            type = "新增充电桩",
            description = "'添加了一个充电桩，编号为：'+ #chargingAddPileDTO.pileNo"
    )
    @PostMapping()
    Result<?> addChargingPile(@RequestBody ChargingPileAddAndUpdateDTO chargingAddPileDTO) {
        chargingPileService.addChargingPile(chargingAddPileDTO);
        return Result.success();
    }

    @LogRecord(
            module = "充电桩管理",
            type = "修改充电桩",
            description = "'修改了充电桩ID为：'+ #id + ' 的信息'"
    )
    @PutMapping("/{id}")
    Result<?> updateChargingPile(@PathVariable Integer id, @RequestBody ChargingPileAddAndUpdateDTO chargingUpdatePileDTO) {
        chargingPileService.updateChargingPile(id, chargingUpdatePileDTO);
        return Result.success();
    }

    @LogRecord(
            module = "充电桩管理",
            type = "删除充电桩",
            description = "'删除了一个充电桩，ID为：'+ #id"
    )
    @DeleteMapping("/{id}")
    Result<?> deleteChargingPile(@PathVariable Integer id) {
        chargingPileService.deleteChargingPile(id);
        return Result.success();
    }

}
