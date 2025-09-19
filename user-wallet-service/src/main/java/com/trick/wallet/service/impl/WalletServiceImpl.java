package com.trick.wallet.service.impl;

import com.trick.common.exception.BusinessException;
import com.trick.common.result.Result;
import com.trick.wallet.client.MarketingClient;
import com.trick.wallet.mapper.WalletMapper;
import com.trick.wallet.model.dto.AmountDTO;
import com.trick.wallet.model.dto.CouponDTO;
import com.trick.wallet.model.pojo.TransactionLog;
import com.trick.wallet.service.TransactionLogService;
import com.trick.wallet.service.WalletService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class WalletServiceImpl implements WalletService {
    private static final int DISCOUNT_COUPONS = 1;//折扣券
    private static final int VOUCHERS = 2;//代金券

    @Autowired
    private TransactionLogService transactionLogService;
    @Autowired
    private WalletMapper walletMapper;
    @Autowired
    private MarketingClient marketingClient;
    @Autowired
    private RedissonClient redissonClient;

    //获取个人余额
    @Override
    public BigDecimal getWallet(Integer id) {
        return walletMapper.getWallet(id);
    }

    //账户余额扣款
    @Override
    public void deductAmount(Integer id, BigDecimal amount) {
        walletMapper.updateBalance(id, amount);
    }

    //充值模块（钱包充值）
    //目前为模拟充值
    @Override
    @GlobalTransactional
    public void recharge(Integer id, AmountDTO amountDTO) {

        //向钱包充值的金额
        BigDecimal amount = amountDTO.getAmount();
        //优惠后实际消费的金额
        BigDecimal amountTemp = null;

        // 1. 校验参数
        if (id == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(422, "充值金额必须大于0");
        }

        // 2. 生成交易流水号
        String transactionNo = "TX" + System.currentTimeMillis() + (int) (Math.random() * 1000);

        // 3. 插入交易记录
        TransactionLog logT = new TransactionLog();
        logT.setTransactionNo(transactionNo);
        logT.setUserId(id);
        logT.setOrderId(null);
        logT.setAmount(amount);
        logT.setType(1); // 1-充值
        logT.setStatus(0); // 1-成功 0-处理中

        // 4. 更新用户钱包余额
        try {
            //判断是否存在优惠券
            Integer couponId = amountDTO.getCouponId();
            if (couponId != null) {
                //将优惠券上分布式锁，防止多用
                RLock lock = redissonClient.getLock("recharge_lock:" + couponId);

                lock.lock();
                try {
                    //拿取优惠券信息并验证该优惠券是否为用户持有
                    Result<CouponDTO> coupon = marketingClient.getCouponById(couponId);
                    if (coupon.getCode() != 200) {
                        throw new BusinessException(coupon.getMsg());
                    }

                    //如果存在优惠券并且可使用，进行折扣计算
                    if (coupon.getData() != null && coupon.getData().getStatus() == 0) {
                        switch (coupon.getData().getType()) {
                            case DISCOUNT_COUPONS -> amountTemp = RechargeDiscounts(amount, coupon.getData());
                            case VOUCHERS -> amountTemp = RechargeAndPriceReduction(amount, coupon.getData());
                        }
                    } else {
                        throw new BusinessException("优惠券无效");
                    }

                    //todo 调用真实支付机制，使用折扣逻辑后的amountTemp进行支付


                    // 设置该优惠券为已使用
                    Result<Integer> result = marketingClient.setTheCouponStatus(couponId, 1);
                    if (result.getCode() != 200 || result.getData() == 0) {
                        throw new BusinessException(result.getMsg());
                    }

                    logT.setDescription("模拟充值，成功使用优惠券，优惠券ID为：" + couponId);

                } finally {
                    lock.unlock();
                }
            }

            //钱包充值
            walletMapper.updateBalance(id, amount);

        } catch (Exception e) {
            logT.setStatus(2);//失败
            logT.setDescription("模拟充值失败");
            transactionLogService.addLogTransactions(id, logT);

            log.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }

        //添加充值记录
        logT.setStatus(1);//成功
        transactionLogService.addLogTransactions(id, logT);
    }

    //-------------------------优惠券价格计算----------------------------------

    /**
     * 代金券计算价格
     *
     * @param amount    充值金额
     * @param couponDTO 优惠券信息
     */
    private BigDecimal RechargeAndPriceReduction(BigDecimal amount, CouponDTO couponDTO) {
        // 校验是否满足最低消费
        if (couponDTO.getMinSpend() != null && amount.compareTo(couponDTO.getMinSpend()) < 0) {
            return amount;
        }

        // 减免金额
        if (couponDTO.getDiscountAmount() != null) {
            BigDecimal newAmount = amount.subtract(couponDTO.getDiscountAmount());
            return newAmount.compareTo(BigDecimal.ZERO) > 0 ? newAmount : BigDecimal.ZERO;
        }

        return amount;
    }

    /**
     * 折扣券计算价格
     *
     * @param amount    充值金额
     * @param couponDTO 优惠券信息
     */
    private BigDecimal RechargeDiscounts(BigDecimal amount, CouponDTO couponDTO) {
        // 校验是否满足最低消费
        if (couponDTO.getMinSpend() != null && amount.compareTo(couponDTO.getMinSpend()) < 0) {
            return amount;
        }

        if (couponDTO.getDiscountPercent() != null) {
            // 计算折扣后的金额
            BigDecimal discount = amount.multiply(couponDTO.getDiscountPercent());

            // 如果有最高折扣金额限制
            if (couponDTO.getMaxDiscountAmount() != null && discount.compareTo(couponDTO.getMaxDiscountAmount()) > 0) {
                discount = couponDTO.getMaxDiscountAmount();
            }

            BigDecimal newAmount = amount.subtract(discount);
            return newAmount.compareTo(BigDecimal.ZERO) > 0 ? newAmount : BigDecimal.ZERO;
        }

        return amount;
    }

}
