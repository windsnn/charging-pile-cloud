package com.trick.backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.trick.backend.common.exception.BusinessException;
import com.trick.backend.common.result.PageResult;
import com.trick.backend.mapper.ChargingPileMapper;
import com.trick.backend.mapper.FaultReportMapper;
import com.trick.backend.model.dto.FaultReportAddDTO;
import com.trick.backend.model.dto.FaultReportQueryDTO;
import com.trick.backend.model.dto.FaultReportUpdateDTO;
import com.trick.backend.model.pojo.FaultReport;
import com.trick.backend.model.vo.FaultReportVO;
import com.trick.backend.service.FaultReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FaultReportServiceImpl implements FaultReportService {
    @Autowired
    private FaultReportMapper faultReportMapper;
    @Autowired
    private ChargingPileMapper pileMapper;
    @Autowired
    private ObjectMapper objectMapper;

    //分页条件查询
    @Override
    public PageResult<FaultReportVO> getFaultReportsByPage(FaultReportQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<FaultReportVO> list = faultReportMapper.getAllFaultReport(queryDTO);
        return jsonConversionAndPaged(list);
    }

    //根据ID更新处理状态（暂定）
    @Override
    public void updateFaultReport(Integer id, FaultReportUpdateDTO updateDTO) {
        updateDTO.setId(id);
        updateDTO.setUpdateTime(LocalDateTime.now());

        faultReportMapper.updateFaultReport(updateDTO);
    }

    //用户提交报修报告
    @Override
    public void addFaultReport(FaultReportAddDTO dto) {
        if (pileMapper.getChargingPileById(dto.getPileId()) == null) {
            throw new BusinessException("不存在的充电桩");
        }

        FaultReport faultReport = new FaultReport();
        faultReport.setUserId(dto.getUserId());
        faultReport.setPileId(dto.getPileId());
        faultReport.setFaultType(dto.getFaultType());
        faultReport.setDescription(dto.getDescription());

        try {
            faultReport.setImages(objectMapper.writeValueAsString(dto.getImages()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("图片列表转JSON失败", e);
        }

        faultReportMapper.addFaultReport(faultReport);
    }

    @Override
    //获取我的报修列表
    public PageResult<FaultReportVO> getWxFaultReports(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<FaultReportVO> list = faultReportMapper.getWxFaultReport(userId);
        return jsonConversionAndPaged(list);
    }

    //json转化为List并分页
    private PageResult<FaultReportVO> jsonConversionAndPaged(List<FaultReportVO> list) {
        PageInfo<FaultReportVO> pageInfo = new PageInfo<>(list);

        List<FaultReportVO> records = pageInfo.getList();
        for (FaultReportVO faultReportVO : records) {
            String imagesJson = faultReportVO.getImagesJson();
            if (imagesJson != null && !imagesJson.isEmpty()) {
                try {
                    faultReportVO.setImages(objectMapper.readValue(imagesJson, new TypeReference<>() {
                    }));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            faultReportVO.setImagesJson(null);
        }

        long total = pageInfo.getTotal();

        return new PageResult<>(total, records);
    }
}
