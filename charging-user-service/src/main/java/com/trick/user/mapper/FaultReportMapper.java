package com.trick.user.mapper;

import com.trick.user.model.pojo.FaultReport;
import com.trick.user.model.vo.FaultReportVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FaultReportMapper {

    //添加报修单
    void addFaultReport(FaultReport faultReport);

    //获取我的报修列表
    List<FaultReportVO> getWxFaultReport(Integer userId);
}
