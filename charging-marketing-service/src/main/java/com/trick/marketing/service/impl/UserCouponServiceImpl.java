package com.trick.marketing.service.impl;

import com.trick.common.exception.BusinessException;
import com.trick.marketing.mapper.UserCouponMapper;
import com.trick.marketing.model.dto.CouponsDTO;
import com.trick.marketing.model.pojo.Coupons;
import com.trick.marketing.model.pojo.UserCoupons;
import com.trick.marketing.model.vo.wxCouponsVO.CouponBaseVO;
import com.trick.marketing.model.vo.wxCouponsVO.DiscountCouponVO;
import com.trick.marketing.model.vo.wxCouponsVO.VouchersVO;
import com.trick.marketing.service.CouponService;
import com.trick.marketing.service.UserCouponService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class UserCouponServiceImpl implements UserCouponService {
    private static final int ENABLED = 1; //已启用

    private static final int FIXED_DATE = 1; //固定过期时间类型
    private static final int DYNAMIC_DATES = 2; //动态过期时间类型

    private static final int DISCOUNT_COUPONS = 1;//折扣券
    private static final int VOUCHERS = 2;//代金券

    @Autowired
    private UserCouponMapper userCouponMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private CouponService couponService;

    //todo 用户优惠券中心 获取所有可用优惠券

    //todo 查询我的所有优惠券

    /**
     * 用户添加优惠券到账户
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCouponToUser(Integer userId, Integer couponId) {
        //使用Redisson分布式锁保证多实例下的一人一票
        RLock lock = redissonClient.getLock("couponLock:" + userId);

        // 尝试获取锁，最多等待10秒，否则返回false;引入看门狗机制
        boolean locked;
        try {
            locked = lock.tryLock(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取锁失败");
        }

        //如果存在锁
        if (locked) {
            try {
                //1 查询是否已经领取过该ID优惠券
                if (hasUserClaimedCoupon(userId, couponId)) {
                    throw new BusinessException("您已领取过该优惠券");
                }

                //2 查询该优惠券信息
                Coupons coupons = couponService.getCouponById(couponId);
                if (coupons == null) {
                    throw new BusinessException("不存在的优惠券");
                }
                if (!Objects.equals(coupons.getStatus(), ENABLED)) {
                    throw new BusinessException("该优惠券已不可领取");
                }

                //3 更新库存（原子更新防止超卖）
                try {
                    couponService.updateStock(couponId);
                } catch (Exception e) {
                    throw new BusinessException("该优惠券已被领取完");
                }

                //4 进行用户绑定
                LocalDateTime now = LocalDateTime.now();
                UserCoupons userCoupons = new UserCoupons();

                userCoupons.setUserId(userId);
                userCoupons.setCouponId(couponId);
                userCoupons.setClaimedAt(now);
                //根据优惠券有效期类型绑定不同过期时间
                switch (coupons.getValidityType()) {
                    case FIXED_DATE -> userCoupons.setExpireAt(coupons.getValidEndTime());
                    case DYNAMIC_DATES -> userCoupons.setExpireAt(now.plusDays(coupons.getDaysAfterClaim()));
                }

                //5 持久化数据库
                userCouponMapper.addCouponToUser(userCoupons);

            } catch (BusinessException e) {
                throw new BusinessException(e.getMessage());
            } finally {
                lock.unlock();
            }

        } else {
            throw new BusinessException("操作频繁，请稍后再试");
        }
    }

    /**
     * 根据优惠券类型的不同，返回不同的VO
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 优惠券信息
     */
    @Override
    public CouponBaseVO getCouponById(Integer userId, Integer couponId) {
        CouponsDTO couponsDTO = userCouponMapper.getCouponById(userId, couponId);

        if (couponsDTO == null) {
            throw new BusinessException("该用户不拥有该优惠券");
        }

        //根据type的不同拷贝到不同vo
        switch (couponsDTO.getType()) {
            case DISCOUNT_COUPONS -> {
                DiscountCouponVO discountCouponVO = new DiscountCouponVO();
                BeanUtils.copyProperties(couponsDTO, discountCouponVO);
                return discountCouponVO;
            }
            case VOUCHERS -> {
                VouchersVO vouchersVO = new VouchersVO();
                BeanUtils.copyProperties(couponsDTO, vouchersVO);
                return vouchersVO;
            }
        }

        throw new RuntimeException("未知异常");

    }

    /**
     * 查询数据库该用户是否拥有该优惠券
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 是否拥有
     */
    @Override
    public boolean hasUserClaimedCoupon(Integer userId, Integer couponId) {
        return userCouponMapper.hasUserClaimedCoupon(userId, couponId);
    }

    /**
     * 更新用户优惠券状态
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @param status   优惠券状态
     */
    @Override
    public Integer updateCouponStatus(Integer userId, Integer couponId, Integer status) {
        return userCouponMapper.updateCouponStatus(userId, couponId, status);
    }
}
