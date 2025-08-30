package com.trick.backend.mapper;

import com.trick.backend.model.dto.ChargingOrderAddDTO;
import com.trick.backend.model.pojo.ChargingOrder;
import com.trick.backend.model.vo.ChargingOrderVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChargingOrderMapper {

    void addOrder(ChargingOrderAddDTO orderAddDTO);

    ChargingOrder getOrder(String orderNo);

    void updateByOrderNo(ChargingOrder order);

    List<ChargingOrderVO> getOngoing(Integer userId);

    List<ChargingOrderVO> getAllOrderByUserId(Integer userId);
}
