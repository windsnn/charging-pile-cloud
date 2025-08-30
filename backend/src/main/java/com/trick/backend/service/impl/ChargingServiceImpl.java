package com.trick.backend.service.impl;

import com.trick.backend.common.WebSocketSimulate.ChargingSimulationService;
import com.trick.backend.common.exception.BusinessException;
import com.trick.backend.mapper.ChargingOrderMapper;
import com.trick.backend.mapper.ChargingPileMapper;
import com.trick.backend.mapper.TransactionLogMapper;
import com.trick.backend.mapper.UserMapper;
import com.trick.backend.model.dto.ChargingDTO;
import com.trick.backend.model.dto.ChargingOrderAddDTO;
import com.trick.backend.model.dto.ChargingPileAddAndUpdateDTO;
import com.trick.backend.model.pojo.ChargingOrder;
import com.trick.backend.model.pojo.ChargingPile;
import com.trick.backend.model.pojo.TransactionLog;
import com.trick.backend.model.vo.ChargingPileVO;
import com.trick.backend.service.ChargingPileService;
import com.trick.backend.service.ChargingService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ChargingServiceImpl implements ChargingService {
    @Autowired
    private ChargingPileService chargingPileService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ChargingSimulationService simulationService;
    @Autowired
    private ChargingPileMapper pileMapper;
    @Autowired
    private ChargingOrderMapper orderMapper;
    @Autowired
    private TransactionLogMapper logMapper;
    @Autowired
    private RedissonClient redissonClient;

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
                    throw new BusinessException("充电桩不存在或正忙！");
                }

                BigDecimal balance = userMapper.getWallet(userId);
                if (balance.compareTo(new BigDecimal("10.00")) < 0) {
                    throw new BusinessException("余额不足10元，请充值后再开始充电！");
                }

                // 更新充电桩状态为“充电中”
                ChargingPileAddAndUpdateDTO dto = new ChargingPileAddAndUpdateDTO();
                dto.setStatus(1); // 1-充电中
                dto.setId(pileId);
                pileMapper.updateChargingPile(dto);

                // 创建订单
                ChargingOrderAddDTO orderAddDTO = new ChargingOrderAddDTO();
                String orderNo = UUID.randomUUID().toString().replace("-", "");
                orderAddDTO.setOrderNo(orderNo);
                orderAddDTO.setUserId(userId);
                orderAddDTO.setPileId(pileId); //该id为充电桩id
                orderAddDTO.setStartTime(LocalDateTime.now());
                orderAddDTO.setStatus(0); // 0-进行中
                orderMapper.addOrder(orderAddDTO);

                // 异步启动充电模拟
                simulationService.startSimulationCharging(orderNo, userId, balance, pile.getPowerRate(), pile.getPricePerKwh());
                return orderNo;
            } catch (BusinessException e) {
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
    public String stopChargingByUser(Integer userId, ChargingDTO chargingDTO) {
        //获取订单号
        String orderNo = chargingDTO.getOrderNo();

        // 先发送停止指令给模拟器
        simulationService.stopCharging(orderNo);

        // 调用统一的结算逻辑
        return finalizeCharging(userId, orderNo);
    }

    //核心逻辑（余额不足停止充电）
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void stopChargingDueToInsufficientBalance(Integer userId, String orderNo) {
        simulationService.stopCharging(orderNo);

        finalizeCharging(userId, orderNo);
    }

    //核心逻辑（订单结算服务）
    @Override
    public String finalizeCharging(Integer userId, String orderNo) {
        ChargingOrder order = orderMapper.getOrder(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在或不属于该用户");
        }

        // 如果订单已完成，直接返回，防止重复处理
        if (order.getStatus() != 0) {
            throw new BusinessException("该订单已完成");
        }

        long duration = java.time.Duration.between(order.getStartTime(), LocalDateTime.now()).getSeconds();
        ChargingPile pile = pileMapper.getChargingPileById(order.getPileId());

        // 精确计算最终费用
        BigDecimal powerConsumed = pile.getPowerRate().multiply(new BigDecimal(duration))
                .divide(new BigDecimal("3600"), 3, java.math.RoundingMode.HALF_UP);
        BigDecimal totalFee = powerConsumed.multiply(pile.getPricePerKwh()).setScale(2, java.math.RoundingMode.HALF_UP);

        // 最终费用不能超过用户余额，以用户余额为准，防止扣成负数
        BigDecimal actualDeduction = totalFee.min(userMapper.getWallet(userId));

        // 更新订单
        order.setEndTime(LocalDateTime.now());
        order.setDuration((int) duration);
        order.setPowerConsumed(powerConsumed);
        order.setTotalFee(actualDeduction); // 记录实际扣款金额
        order.setStatus(1); // 1-已完成
        orderMapper.updateByOrderNo(order);

        // 从用户余额扣款
        if (actualDeduction.compareTo(BigDecimal.ZERO) > 0) {
            userMapper.decreaseBalance(userId, actualDeduction);

            // 插入交易流水
            TransactionLog log = new TransactionLog();
            String transactionNo = "TX" + System.currentTimeMillis() + (int) (Math.random() * 1000);
            log.setTransactionNo(transactionNo);
            log.setUserId(userId);
            log.setOrderId(order.getId());
            log.setAmount(actualDeduction);
            log.setType(2); // 2-充电支付
            log.setStatus(1);
            log.setDescription("充电消费，订单号：" + orderNo);
            logMapper.addLogT(log);
        }

        // 恢复充电桩状态
        ChargingPileAddAndUpdateDTO dto = new ChargingPileAddAndUpdateDTO();
        dto.setStatus(0);
        dto.setId(order.getPileId());
        pileMapper.updateChargingPile(dto);

        return orderNo;
    }
}
