package com.trick.order.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.trick.common.result.PageResult;
import com.trick.order.mapper.ChargingOrderMapper;
import com.trick.order.model.dto.ChargingOrderAddDTO;
import com.trick.order.model.vo.ChargingOrderVO;
import com.trick.order.service.ChargingOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChargingOrderServiceImpl implements ChargingOrderService {
    @Autowired
    private ChargingOrderMapper orderMapper;

    @Override
    //当前用户正在进行的订单
    public List<ChargingOrderVO> getOngoing(Integer userId) {
        return orderMapper.getOngoing(userId);
    }

    @Override
    public PageResult<ChargingOrderVO> getPagedOrder(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ChargingOrderVO> list = orderMapper.getAllOrderByUserId(userId);
        PageInfo<ChargingOrderVO> pageInfo = new PageInfo<>(list);

        List<ChargingOrderVO> records = pageInfo.getList();
        long total = pageInfo.getTotal();

        return new PageResult<>(total, records);
    }

    @Override
    public void addOrder(ChargingOrderAddDTO chargingOrderAddDTO) {
        orderMapper.addOrder(chargingOrderAddDTO);
    }
}
