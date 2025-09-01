package com.trick.order.service;

public interface ChargingService {
    //核心逻辑（订单结算服务）
    String finalizeCharging(Integer userId, String orderNo);
}
