package com.trick.backend.controller.admin;

import com.trick.backend.common.aop.LogRecord;
import com.trick.backend.common.result.PageResult;
import com.trick.backend.common.result.Result;
import com.trick.backend.model.dto.ChargingPileAddAndUpdateDTO;
import com.trick.backend.model.dto.ChargingPileQueryDTO;
import com.trick.backend.model.pojo.ChargingPile;
import com.trick.backend.model.vo.ChargingPileVO;
import com.trick.backend.service.ChargingPileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/piles")
public class ChargingPileController {
    @Autowired
    private ChargingPileService chargingPileService;

    @GetMapping()
    Result<PageResult<ChargingPile>> getChargingPilesByPage(ChargingPileQueryDTO queryDTO) {
        return Result.success(chargingPileService.getChargingPilesByPage(queryDTO));
    }

    @GetMapping("/{id}")
    Result<ChargingPileVO> getChargingPileById(@PathVariable Integer id) {
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
