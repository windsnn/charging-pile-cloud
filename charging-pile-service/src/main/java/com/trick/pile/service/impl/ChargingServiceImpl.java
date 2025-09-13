package com.trick.pile.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trick.common.exception.BusinessException;
import com.trick.common.result.Result;
import com.trick.common.utils.ThreadLocalUtil;
import com.trick.pile.WebSocketSimulate.ChargingSimulationService;
import com.trick.pile.client.AsyncOrderClient;
import com.trick.pile.client.OrderClient;
import com.trick.pile.client.UserClient;
import com.trick.pile.mapper.ChargingPileMapper;
import com.trick.pile.model.dto.ChargingDTO;
import com.trick.pile.model.dto.ChargingOrderAddDTO;
import com.trick.pile.model.dto.ChargingPileAddAndUpdateDTO;
import com.trick.pile.model.vo.ChargingPileVO;
import com.trick.pile.service.ChargingPileService;
import com.trick.pile.service.ChargingService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ChargingServiceImpl implements ChargingService {
    @Autowired
    private ChargingPileService chargingPileService;
    @Autowired
    private UserClient userClient;
    @Autowired
    private ChargingSimulationService simulationService;
    @Autowired
    private OrderClient orderClient;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private AsyncOrderClient asyncOrderClient;

    // 核心业务（充电逻辑）
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startCharging(Integer userId, ChargingDTO chargingDTO) {
        Integer pileId = chargingDTO.getPileId();
        RLock lock = redissonClient.getLock("lock:pile:" + pileId);
        boolean locked;

        // 尝试获取锁，最多等待10秒，引入看门狗机制
        try {
            locked = lock.tryLock(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("操作频繁，请稍后再试");
        }

        if (locked) {
            try {
                ChargingPileVO pile = chargingPileService.getChargingPileById(pileId);

                if (pile == null || pile.getStatus() != 0) {
                    throw new BusinessException(400, "充电桩不存在或正忙！");
                }

                Result<Map<String, BigDecimal>> result1 = userClient.getWallet();
                if (result1.getCode() != 200) {
                    throw new BusinessException(result1.getMsg());
                }

                BigDecimal balance = result1.getData().get("balance");
                if (balance.compareTo(new BigDecimal("10.00")) < 0) {
                    throw new BusinessException("余额不足10元，请充值后再开始充电！");
                }

                // 更新充电桩状态为“充电中”
                ChargingPileAddAndUpdateDTO dto = new ChargingPileAddAndUpdateDTO();
                dto.setStatus(1); // 1-充电中
                chargingPileService.updateChargingPile(pileId, dto);

                // 创建订单
                ChargingOrderAddDTO orderAddDTO = new ChargingOrderAddDTO();
                String orderNo = UUID.randomUUID().toString().replace("-", "");
                orderAddDTO.setOrderNo(orderNo);
                orderAddDTO.setPileId(pileId); //该id为充电桩id
                orderAddDTO.setStartTime(LocalDateTime.now());
                orderAddDTO.setStatus(0); // 0-进行中

                Result<?> result = orderClient.addOrder(orderAddDTO);
                if (result.getCode() != 200) {
                    throw new BusinessException(result.getMsg());
                }

                // 异步启动充电模拟
                simulationService.startSimulationCharging(orderNo, userId, balance, pile.getPowerRate(), pile.getPricePerKwh());
                return orderNo;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock(); // 释放锁
            }
        } else {
            throw new BusinessException("操作频繁，请稍后再试！");
        }

    }

    //核心逻辑（用户主动结束充电）
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> stopChargingByUser(Integer userId, ChargingDTO chargingDTO) {
        //获取订单号
        String orderNo = chargingDTO.getOrderNo();

        // 先发送停止指令给模拟器
        simulationService.stopCharging(orderNo);

        // 调用统一的结算逻辑
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderNo", orderNo);

        Result<Map<String, String>> result = orderClient.finalizeCharging(hashMap);
        if (result.getCode() != 200) {
            throw new BusinessException(result.getMsg());
        }

        return result.getData();
    }

    //核心逻辑（余额不足停止充电）
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void stopChargingDueToInsufficientBalance(Integer userId, String orderNo) {
        simulationService.stopCharging(orderNo);

        //设置当前线程ThreadLocal（过验证逻辑）
        Map<String, Object> local = new HashMap<>();
        local.put("X-User-Id", userId);
        ThreadLocalUtil.setContext(local);

        // 自动调用统一的结算逻辑
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderNo", orderNo);
        Result<Map<String, String>> result = asyncOrderClient.asyncFinalizeCharging(hashMap);
        if (result.getCode() != 200) {
            throw new BusinessException(result.getMsg());
        }
    }

}
