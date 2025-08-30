package com.trick.backend.service;

import com.trick.backend.common.result.PageResult;
import com.trick.backend.model.dto.ChargingDTO;
import com.trick.backend.model.vo.ChargingOrderVO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChargingOrderService {
    //当前用户正在进行的订单
    List<ChargingOrderVO> getOngoing(Integer userId);

    //获取分页订单
    PageResult<ChargingOrderVO> getPagedOrder(Integer userId, Integer pageNum, Integer pageSize);
}
