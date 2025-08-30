package com.trick.backend.controller.wx;

import com.trick.backend.common.result.PageResult;
import com.trick.backend.common.result.Result;
import com.trick.backend.common.utils.ThreadLocalUtil;
import com.trick.backend.model.dto.FaultReportAddDTO;
import com.trick.backend.model.dto.FaultReportQueryDTO;
import com.trick.backend.model.vo.FaultReportVO;
import com.trick.backend.service.FaultReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wx/faults")
public class WxFaultReportController {
    @Autowired
    private FaultReportService faultReportService;

    @PostMapping("/report")
    //用户提交故障报告
    public Result<?> report(@RequestBody FaultReportAddDTO dto) {
        // token获取UserId
        Integer userId = (Integer) ThreadLocalUtil.getContext().get("id");
        dto.setUserId(userId);

        faultReportService.addFaultReport(dto);
        return Result.success();
    }

    @GetMapping
    //获取我的报修记录
    public Result<PageResult<FaultReportVO>> getFaultReport(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        // token获取UserId
        Integer userId = (Integer) ThreadLocalUtil.getContext().get("id");

        return Result.success(faultReportService.getWxFaultReports(userId, pageNum, pageSize));
    }
}
