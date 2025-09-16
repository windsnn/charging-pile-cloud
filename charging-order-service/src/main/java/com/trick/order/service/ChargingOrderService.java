package com.trick.order.service;

import com.trick.common.result.PageResult;
import com.trick.order.model.dto.ChargingOrderAddDTO;
import com.trick.order.model.pojo.ChargingOrder;
import com.trick.order.model.vo.ChargingOrderVO;

import java.util.List;

public interface ChargingOrderService {
    //当前用户正在进行的订单
    List<ChargingOrderVO> getOngoing(Integer userId);

    //获取分页订单
    PageResult<ChargingOrderVO> getPagedOrder(Integer userId, Integer pageNum, Integer pageSize);

    //添加订单
    void addOrder(ChargingOrderAddDTO chargingOrderAddDTO);

    //更新订单
    void updateByOrderNo(ChargingOrder order);

    //获取订单
    ChargingOrder getOrder(String orderNo);
}
