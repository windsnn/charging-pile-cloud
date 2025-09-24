package com.trick.marketing.consumer;

import com.trick.marketing.mapper.UserCouponMapper;
import com.trick.marketing.model.dto.CouponConsumerDTO;
import com.trick.marketing.model.pojo.UserCoupons;
import com.trick.marketing.service.CouponService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.trick.marketing.constants.CouponConstants.DYNAMIC_DATE;
import static com.trick.marketing.constants.CouponConstants.FIXED_DATE;

@Component
public class CouponsConsumer {
    @Autowired
    private CouponService couponService;
    @Autowired
    private UserCouponMapper userCouponMapper;

    @RabbitListener(bindings = {@QueueBinding(
            value = @Queue(name = "coupon.stock.queue1"),
            exchange = @Exchange(name = "coupon.direct"),
            key = "stock.decr"
    )})
    public void updateStockConsumer(CouponConsumerDTO dto) {
        LocalDateTime now = LocalDateTime.now();

        //更新优惠券库存
        couponService.updateStock(dto.getCouponId());
        // 进行用户绑定
        UserCoupons userCoupons = new UserCoupons();
        userCoupons.setUserId(dto.getUserId());
        userCoupons.setCouponId(dto.getCouponId());
        userCoupons.setClaimedAt(now);
        // 根据优惠券有效期类型绑定不同过期时间
        switch (dto.getValidityType()) {
            case FIXED_DATE -> userCoupons.setExpireAt(dto.getValidEndTime());
            case DYNAMIC_DATE -> userCoupons.setExpireAt(now.plusDays(dto.getDaysAfterClaim()));
        }

        // 持久化数据库
        userCouponMapper.addCouponToUser(userCoupons);
    }


}
