package com.trick.backend.common.WebSocketSimulate;

import com.trick.backend.service.ChargingOrderService;
import com.trick.backend.service.ChargingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ChargingSimulationService {
    // 用于从外部停止模拟任务的标志位
    private static final Map<String, Boolean> stopFlags = new ConcurrentHashMap<>();

    @Lazy
    @Autowired
    private ChargingService chargingService;
    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 开始一个异步的充电模拟任务
     *
     * @param orderNo     订单ID
     * @param powerRate   充电桩功率 (kW)
     * @param pricePerKwh 每度电价格
     */
    @Async
    public void startSimulationCharging(String orderNo, Integer userId, BigDecimal initialBalance, BigDecimal powerRate, BigDecimal pricePerKwh) {
        log.info("【模拟充电】订单 {} 开始，功率: {} kW", orderNo, powerRate);
        stopFlags.put(orderNo, false);

        int durationInSeconds = 0; // 已充电秒数
        BigDecimal powerConsumed = BigDecimal.ZERO; // 已充电量
        BigDecimal currentFee; // 当前费用

        Map<String, String> endMessage = new HashMap<>();
        endMessage.put("status", "SUCCESS");

        // 模拟充电过程，每2秒推送一次数据
        while (!stopFlags.getOrDefault(orderNo, false)) {
            try {
                Thread.sleep(2000); // 模拟时间流逝

                durationInSeconds += 2;

                // 计算此期间的充电量 (功率 * 时间)
                // (powerRate / 3600) * 2 秒的电量
                BigDecimal incrementKwh = powerRate.multiply(new BigDecimal("2"))
                        .divide(new BigDecimal("3600"), 4, RoundingMode.HALF_UP);
                powerConsumed = powerConsumed.add(incrementKwh);

                // 计算当前消耗费用
                currentFee = powerConsumed.multiply(pricePerKwh).setScale(2, RoundingMode.HALF_UP);

                //余额监控逻辑
                if (currentFee.compareTo(initialBalance) > 0) {
                    log.warn("【余额不足】订单 {}, 当前费用 {} 已超出初始余额 {}，自动停止充电。", orderNo, currentFee, initialBalance);

                    // 触发自动停止流程
                    endMessage.put("status", "STOPPED");
                    chargingService.stopChargingDueToInsufficientBalance(userId, orderNo);
                    break; // 跳出循环
                }

                // 准备要推送的数据
                Map<String, Object> data = new HashMap<>();
                data.put("duration", durationInSeconds); // 充电时长（秒）
                data.put("powerConsumed", powerConsumed.setScale(3, RoundingMode.HALF_UP)); // 充电量 (kWh)
                data.put("fee", currentFee); // 当前消耗费用
                data.put("balanceLeft", initialBalance
                        .subtract(currentFee)
                        .setScale(2, RoundingMode.HALF_UP)); // 剩余可用金额

                // 通过WebSocket推送给客户端
                webSocketServer.sendTo(orderNo, data);
                log.debug("【模拟充电】推送数据 -> 订单: {}, 数据: {}", orderNo, data);

            } catch (InterruptedException e) {
                log.error("【模拟充电】线程被中断，订单: {}", orderNo, e);
                Thread.currentThread().interrupt();
                break;
            }
        }

        // 循环结束后，清理标志位
        stopFlags.remove(orderNo);
        log.info("【模拟充电】订单 {} 结束。", orderNo);

        // 发送一个最终的“充电结束”消息
        webSocketServer.sendTo(orderNo, endMessage);
    }

    /**
     * 从外部停止一个正在进行的充电模拟
     *
     * @param orderNo 订单ID
     */
    public void stopCharging(String orderNo) {
        if (stopFlags.containsKey(orderNo)) {
            stopFlags.put(orderNo, true);
            log.info("【模拟充电】收到停止指令，订单: {}", orderNo);
        }
    }
}
