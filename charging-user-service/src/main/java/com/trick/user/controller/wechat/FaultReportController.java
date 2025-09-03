package com.trick.user.controller.wechat;

import com.trick.common.result.PageResult;
import com.trick.common.result.Result;
import com.trick.common.utils.ThreadLocalUtil;
import com.trick.user.model.dto.FaultReportAddDTO;
import com.trick.user.model.vo.FaultReportVO;
import com.trick.user.service.FaultReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wx/faults")
public class FaultReportController {
    @Autowired
    private FaultReportService faultReportService;

    @PostMapping("/report")
    //用户提交故障报告
    public Result<?> report(@RequestBody FaultReportAddDTO dto) {
        // 线程获取UserId
        Integer userId = ThreadLocalUtil.getUserId();
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
        // 线程获取UserId
        Integer userId = ThreadLocalUtil.getUserId();

        return Result.success(faultReportService.getWxFaultReports(userId, pageNum, pageSize));
    }
}
