package com.trick.pile.service;

import com.trick.pile.model.dto.ChargingDTO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface ChargingService {
    //核心逻辑（用户开始充电）
    String startCharging(Integer UserId, ChargingDTO chargingDTO);

    //核心逻辑（用户主动结束充电）
    Map<String,String> stopChargingByUser(Integer userId, ChargingDTO chargingDTO);

    //核心逻辑（余额不足停止充电）
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void stopChargingDueToInsufficientBalance(Integer userId, String orderNo);
}
