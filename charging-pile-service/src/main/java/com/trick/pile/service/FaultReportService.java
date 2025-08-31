package com.trick.pile.service;


import com.trick.common.result.PageResult;
import com.trick.pile.model.dto.FaultReportAddDTO;
import com.trick.pile.model.dto.FaultReportQueryDTO;
import com.trick.pile.model.dto.FaultReportUpdateDTO;
import com.trick.pile.model.vo.FaultReportVO;

public interface FaultReportService {

    PageResult<FaultReportVO> getFaultReportsByPage(FaultReportQueryDTO queryDTO);

    void updateFaultReport(Integer id, FaultReportUpdateDTO updateDTO);

    void addFaultReport(FaultReportAddDTO dto);

    PageResult<FaultReportVO> getWxFaultReports(Integer userId, Integer pageNum, Integer pageSize);

}
