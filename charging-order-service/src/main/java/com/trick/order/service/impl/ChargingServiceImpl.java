package com.trick.order.service.impl;

import com.trick.common.exception.BusinessException;
import com.trick.common.result.Result;
import com.trick.order.client.PileClient;
import com.trick.order.client.WalletClient;
import com.trick.order.model.dto.AmountDTO;
import com.trick.order.model.pojo.ChargingOrder;
import com.trick.order.model.pojo.TransactionLog;
import com.trick.order.model.vo.ChargingPileVO;
import com.trick.order.service.ChargingOrderService;
import com.trick.order.service.ChargingService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ChargingServiceImpl implements ChargingService {
    @Autowired
    private WalletClient walletClient;
    @Autowired
    private PileClient pileClient;
    @Autowired
    private ChargingOrderService chargingOrderService;

    //核心逻辑（订单结算服务）
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public String finalizeCharging(Integer userId, String orderNo) {
        ChargingOrder order = chargingOrderService.getOrder(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在或不属于该用户");
        }

        // 如果订单已完成，直接返回，防止重复处理
        if (order.getStatus() != 0) {
            throw new BusinessException("该订单已完成");
        }
        long duration = 0;
        BigDecimal powerConsumed = null;
        BigDecimal actualDeduction = null;

        // 1 先设置交易流水
        TransactionLog log = new TransactionLog();
        String transactionNo = "TX" + System.currentTimeMillis() + (int) (Math.random() * 1000);
        log.setTransactionNo(transactionNo);
        log.setUserId(userId);
        log.setOrderId(order.getId());
        log.setAmount(actualDeduction);
        log.setType(2); // 2-充电支付
        log.setStatus(1);
        log.setDescription("充电消费，订单号：" + orderNo);

        // 2 先设置订单数据
        order.setEndTime(LocalDateTime.now());
        order.setDuration((int) duration);
        order.setPowerConsumed(powerConsumed);
        order.setTotalFee(actualDeduction); // 记录实际扣款金额
        order.setStatus(1); // 1-已完成

        try {
            // 3 计算费用
            duration = Duration.between(order.getStartTime(), LocalDateTime.now()).getSeconds();

            Result<ChargingPileVO> result = pileClient.getChargingPile(order.getPileId());
            if (result.getCode() != 200) {
                throw new BusinessException(result.getMsg());
            }
            ChargingPileVO pile = result.getData();

            powerConsumed = pile.getPowerRate().multiply(new BigDecimal(duration))
                    .divide(new BigDecimal("3600"), 3, RoundingMode.HALF_UP);
            BigDecimal totalFee = powerConsumed.multiply(pile.getPricePerKwh()).setScale(2, RoundingMode.HALF_UP);

            // 最终费用不能超过用户余额，以用户余额为准，防止扣成负数
            Result<Map<String, BigDecimal>> wallet = walletClient.getWallet();
            if (wallet.getCode() != 200) {
                throw new BusinessException(wallet.getMsg());
            }
            actualDeduction = totalFee.min(wallet.getData().get("balance"));

            // 4 从用户余额扣款
            if (actualDeduction.compareTo(BigDecimal.ZERO) > 0) {
                //取费用的相反数
                BigDecimal negativeAmount = actualDeduction.negate();
                Result<?> result1 = walletClient.deductAmount(new AmountDTO(negativeAmount));

                if (result1.getCode() != 200) {
                    throw new BusinessException(result1.getMsg());
                }
            }
        } catch (Exception e) {
            log.setStatus(2); //将状态设置为 2 失败
            order.setStatus(1); // 1-已完成
            //添加更新失败的订单、交易
            walletClient.addLogT(log);
            chargingOrderService.updateByOrderNo(order);

            throw new BusinessException(e.getMessage());
        }

        walletClient.addLogT(log);
        chargingOrderService.updateByOrderNo(order);

        // 5 恢复充电桩状态
        if (pileClient.setState(order.getPileId(), 0).getCode() != 200) {
            throw new RuntimeException("充电桩状态恢复失败");
        }

        return orderNo;
    }
}
