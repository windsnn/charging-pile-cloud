package com.trick.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.trick.backend.common.result.PageResult;
import com.trick.backend.mapper.ChargingOrderMapper;
import com.trick.backend.model.vo.ChargingOrderVO;
import com.trick.backend.service.ChargingOrderService;
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
}
