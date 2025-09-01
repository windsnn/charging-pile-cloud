package com.trick.user.service;


import com.trick.common.result.PageResult;
import com.trick.user.model.dto.FaultReportAddDTO;
import com.trick.user.model.dto.FaultReportQueryDTO;
import com.trick.user.model.dto.FaultReportUpdateDTO;
import com.trick.user.model.vo.FaultReportVO;

public interface FaultReportService {

    PageResult<FaultReportVO> getFaultReportsByPage(FaultReportQueryDTO queryDTO);

    void updateFaultReport(Integer id, FaultReportUpdateDTO updateDTO);

    void addFaultReport(FaultReportAddDTO dto);

    PageResult<FaultReportVO> getWxFaultReports(Integer userId, Integer pageNum, Integer pageSize);

}
