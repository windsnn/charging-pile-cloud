package com.trick.marketing.service.impl;

import com.trick.common.exception.BusinessException;
import com.trick.marketing.mapper.CouponMapper;
import com.trick.marketing.mapper.UserCouponMapper;
import com.trick.marketing.model.dto.CouponsDTO;
import com.trick.marketing.model.dto.UpdateCouponForUserDTO;
import com.trick.marketing.model.pojo.Coupons;
import com.trick.marketing.model.pojo.UserCoupons;
import com.trick.marketing.model.vo.CouponHubVO;
import com.trick.marketing.model.vo.wxCouponsVO.CouponBaseVO;
import com.trick.marketing.model.vo.wxCouponsVO.DiscountCouponVO;
import com.trick.marketing.model.vo.wxCouponsVO.VouchersVO;
import com.trick.marketing.service.CouponService;
import com.trick.marketing.service.UserCouponService;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CouponMapper couponMapper;

    //todo 定时任务设置优惠券为过期

    /**
     * 用户添加优惠券到账户
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCouponToUser(Integer userId, Integer couponId) {
        //todo 更改为热点数据redis秒杀+MQ消息队列

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

                LocalDateTime now = LocalDateTime.now();
                LocalDateTime startTime = coupons.getValidStartTime();
                LocalDateTime endTime = coupons.getValidEndTime();

                if (!Objects.equals(coupons.getStatus(), ENABLED)) {
                    throw new BusinessException("该优惠券已不可领取");
                }

                if (startTime.isAfter(now)) {
                    throw new BusinessException("未到该优惠券领取时间");
                } else if (endTime.isBefore(now)) {
                    throw new BusinessException("该优惠券已过期，无法领取");
                }

                //3 更新库存（原子更新防止超卖）
                try {
                    couponService.updateStock(couponId);
                } catch (Exception e) {
                    throw new BusinessException("该优惠券已被领取完");
                }

                //4 进行用户绑定
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

            } finally {
                lock.unlock();
            }

        } else {
            throw new BusinessException("操作频繁，请稍后再试");
        }
    }

    /**
     * 根据优惠券类型返回不同的VO
     * 获取的是用户已有的优惠券信息
     *
     * @param userId   用户ID
     * @param type     优惠券类型
     * @param couponId 优惠券ID
     * @return 优惠券集合
     */
    @Override
    public List<CouponBaseVO> getCoupons(Integer userId, Integer type, Integer couponId) {
        if (userId == null || (type == null && couponId == null)) {
            throw new RuntimeException("参数错误");
        }

        List<CouponsDTO> couponsDTO = userCouponMapper.getCoupons(userId, type, couponId);

        //根据优惠券类型不同，调用方法获取不同VO
        return couponsDTO.stream()
                .map(dto -> getCouponBaseVO(dto.getType(), dto))
                .toList();
    }

    /**
     * 获取优惠券中心的券
     *
     * @return 券
     */
    @Override
    public List<CouponHubVO> getCouponHub() {
        return userCouponMapper.getCouponHub();
    }

    /**
     * 更新用户优惠券数据，为已使用
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @param dto      更新的内容
     */
    @Override
    @Transactional
    public void updateCouponInformationForUser(Integer userId, Integer couponId, UpdateCouponForUserDTO dto) {
        if (dto != null) {
            userCouponMapper.updateCouponInformationForUser(userId, couponId, dto);
        } else {
            throw new BusinessException("传入的数据为空");
        }
    }

//------------------------私人方法---------------------------

    /**
     * 查询数据库该用户是否拥有该优惠券
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 是否拥有
     */
    private boolean hasUserClaimedCoupon(Integer userId, Integer couponId) {
        return userCouponMapper.hasUserClaimedCoupon(userId, couponId);
    }

    /**
     * 根据type的不同拷贝到不同vo
     *
     * @param type       优惠券的类型
     * @param couponsDTO 优惠券的DTO
     * @return 多态返回，向上转型
     */
    private CouponBaseVO getCouponBaseVO(Integer type, CouponsDTO couponsDTO) {
        switch (type) {
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
            default -> throw new BusinessException("不存在的优惠券类型");
        }
    }

}
