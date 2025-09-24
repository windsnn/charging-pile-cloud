package com.trick.marketing.scheduled_tasks;

import com.trick.marketing.mapper.CouponMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CouponTask {
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private RedissonClient redisson;

    /**
     * 定时任务，更新已过期的优惠券状态
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void updateExpiredCouponsStatus() {
        RLock lock = redisson.getLock("CouponTask");

        try {
            if (lock.tryLock(0, 30, TimeUnit.SECONDS)) {
                couponMapper.updateExpiredCoupon();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("锁异常", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }
}
