package com.trick.pile.controller.admin;

import com.trick.common.annotation.LogRecord;
import com.trick.common.result.PageResult;
import com.trick.common.result.Result;
import com.trick.pile.model.dto.FaultReportQueryDTO;
import com.trick.pile.model.dto.FaultReportUpdateDTO;
import com.trick.pile.model.vo.FaultReportVO;
import com.trick.pile.service.FaultReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/faults")
public class AdminFaultReportController {
    @Autowired
    FaultReportService faultReportService;

    @GetMapping()
    Result<PageResult<FaultReportVO>> getFaultReportsByPage(FaultReportQueryDTO queryDTO) {
        return Result.success(faultReportService.getFaultReportsByPage(queryDTO));
    }

    @LogRecord(
            module = "维修记录管理",
            type = "修改维修记录状态",
            description = "'修改了维修记录，记录ID为：'+ #id"
    )
    @PutMapping("{id}")
    Result<?> updateFaultReport(@PathVariable Integer id, @RequestBody FaultReportUpdateDTO updateDTO) {
        faultReportService.updateFaultReport(id, updateDTO);
        return Result.success();
    }
}
