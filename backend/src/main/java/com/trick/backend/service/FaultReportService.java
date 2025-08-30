package com.trick.backend.service;

import com.trick.backend.common.result.PageResult;
import com.trick.backend.model.dto.FaultReportAddDTO;
import com.trick.backend.model.dto.FaultReportQueryDTO;
import com.trick.backend.model.dto.FaultReportUpdateDTO;
import com.trick.backend.model.pojo.FaultReport;
import com.trick.backend.model.vo.FaultReportVO;

public interface FaultReportService {

    PageResult<FaultReportVO> getFaultReportsByPage(FaultReportQueryDTO queryDTO);

    void updateFaultReport(Integer id, FaultReportUpdateDTO updateDTO);

    void addFaultReport(FaultReportAddDTO dto);

    PageResult<FaultReportVO> getWxFaultReports(Integer userId, Integer pageNum, Integer pageSize);

}
