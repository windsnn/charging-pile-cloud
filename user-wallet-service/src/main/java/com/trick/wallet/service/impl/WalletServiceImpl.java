package com.trick.wallet.service.impl;

import com.trick.common.exception.BusinessException;
import com.trick.common.result.Result;
import com.trick.wallet.client.MarketingClient;
import com.trick.wallet.mapper.WalletMapper;
import com.trick.wallet.model.dto.AmountDTO;
import com.trick.wallet.model.dto.CouponDTO;
import com.trick.wallet.model.pojo.TransactionLog;
import com.trick.wallet.model.vo.UpdateCouponForUserVO;
import com.trick.wallet.service.TransactionLogService;
import com.trick.wallet.service.WalletService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

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
    public void recharge(Integer userId, AmountDTO amountDTO) {

        //向钱包充值的金额
        BigDecimal amount = amountDTO.getAmount();
        //优惠后实际消费的金额
        BigDecimal amountTemp = null;

        // 1. 校验参数
        if (userId == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(422, "充值金额必须大于0");
        }

        // 2. 生成交易流水号
        String transactionNo = "TX" + System.currentTimeMillis() + (int) (Math.random() * 1000);

        // 3. 插入交易记录
        TransactionLog logT = new TransactionLog();
        logT.setTransactionNo(transactionNo);
        logT.setUserId(userId);
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

                    //如果存在优惠券并且可使用(status通过数据库userAt计算)，进行折扣计算
                    if (coupon.getData() != null && coupon.getData().getStatus() == 0) {
                        switch (coupon.getData().getType()) {
                            case DISCOUNT_COUPONS -> amountTemp = RechargeDiscounts(amount, coupon.getData());
                            case VOUCHERS -> amountTemp = RechargeAndPriceReduction(amount, coupon.getData());
                        }
                    } else {
                        throw new BusinessException("优惠券无效");
                    }

                    //todo 5 调用真实支付机制，使用折扣逻辑后的amountTemp进行支付

                    //钱包充值
                    walletMapper.updateBalance(userId, amount);

                    //设置交易成功描述,添加充值记录
                    logT.setStatus(1);//成功
                    logT.setDescription("模拟充值，成功使用优惠券，优惠券ID为：" + couponId);
                    transactionLogService.addLogTransactions(userId, logT);

                    //6 更新优惠券信息，记录优惠券的使用时间等（设置该优惠券为已使用）
                    Result<?> result = marketingClient.updateCouponInformationForUser(
                            couponId,
                            new UpdateCouponForUserVO(LocalDateTime.now(), logT.getId()));

                    if (result.getCode() != 200) {
                        throw new BusinessException(result.getMsg());
                    }

                } finally {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            logT.setStatus(2);//失败
            logT.setDescription("模拟充值失败");
            transactionLogService.addLogTransactions(userId, logT);

            log.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }

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
            BigDecimal payableAmount = amount
                    .multiply(couponDTO.getDiscountPercent()) //乘以折扣，如85折
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP); //除以100，四舍五入

            if (couponDTO.getMaxDiscountAmount() != null) {
                BigDecimal discountAmount = amount.subtract(payableAmount); //折扣金额
                if (discountAmount.compareTo(couponDTO.getMaxDiscountAmount()) > 0) {
                    //使用最大折扣金额进行优惠
                    payableAmount = amount.subtract(couponDTO.getMaxDiscountAmount());
                }
            }

            return payableAmount.max(BigDecimal.ZERO);
        }

        return amount;
    }
}