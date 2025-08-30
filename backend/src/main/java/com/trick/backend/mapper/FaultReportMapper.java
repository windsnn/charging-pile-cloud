package com.trick.backend.mapper;

import com.trick.backend.model.dto.FaultReportAddDTO;
import com.trick.backend.model.dto.FaultReportQueryDTO;
import com.trick.backend.model.dto.FaultReportUpdateDTO;
import com.trick.backend.model.pojo.FaultReport;
import com.trick.backend.model.vo.FaultReportVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FaultReportMapper {
    //分页条件查询报修列表（充电桩编号，状态），默认创建时间倒序
    List<FaultReportVO> getAllFaultReport(FaultReportQueryDTO queryDTO);

    //更新处理状态（暂时）
    void updateFaultReport(FaultReportUpdateDTO updateDTO);

    //添加报修单
    void addFaultReport(FaultReport faultReport);

    //获取我的报修列表
    List<FaultReportVO> getWxFaultReport(Integer userId);
}
