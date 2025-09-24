package com.trick.marketing.service.impl;

import com.trick.common.exception.BusinessException;
import com.trick.marketing.enums.CouponStatus;
import com.trick.marketing.mapper.UserCouponMapper;
import com.trick.marketing.model.dto.CouponConsumerDTO;
import com.trick.marketing.model.dto.CouponsDTO;
import com.trick.marketing.model.dto.UpdateCouponForUserDTO;
import com.trick.marketing.model.pojo.Coupons;
import com.trick.marketing.model.vo.CouponHubVO;
import com.trick.marketing.model.vo.wxCouponsVO.CouponBaseVO;
import com.trick.marketing.model.vo.wxCouponsVO.DiscountCouponVO;
import com.trick.marketing.model.vo.wxCouponsVO.VouchersVO;
import com.trick.marketing.service.CouponService;
import com.trick.marketing.service.UserCouponService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.trick.marketing.constants.CouponConstants.*;

@Service
public class UserCouponServiceImpl implements UserCouponService {
    /**
     * lua脚本，扣减库存
     */
    private static final DefaultRedisScript<Long> DECR_STOCK;

    static {
        DECR_STOCK = new DefaultRedisScript<>();
        DECR_STOCK.setLocation(new ClassPathResource("lua/decr_stock.lua"));
        DECR_STOCK.setResultType(Long.class);
    }

    @Autowired
    private UserCouponMapper userCouponMapper;
    @Autowired
    private CouponService couponService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 用户添加优惠券到账户
     * 更改为redis秒杀+MQ消息队列
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     */
    @Override
    public void addCouponToUser(Integer userId, Integer couponId) {
        //1 查询该优惠券信息
        //优惠券不存在
        Coupons coupons = couponService.getCouponById(couponId);
        if (coupons == null) {
            throw new BusinessException("不存在的优惠券");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = coupons.getValidStartTime();
        LocalDateTime endTime = coupons.getValidEndTime();

        //优惠券未启用
        if (!Objects.equals(CouponStatus.of(coupons.getStatus()), CouponStatus.ENABLED)) {
            throw new BusinessException("该优惠券已不可领取");
        }

        //优惠券未到开启时间
        if (startTime.isAfter(now)) {
            throw new BusinessException("未到该优惠券领取时间");
        } else if (endTime.isBefore(now)) {
            throw new BusinessException("该优惠券已过期，无法领取");
        }

        //2 使用lua脚本完成对库存的原子性扣除，并实现一人一单
        //获取key集合
        String code = coupons.getCode();
        String stockKey = COUPON_STOCK_KEY + code;
        String userKey = COUPON_USER_KEY + code;

        //执行lua脚本
        long execute = stringRedisTemplate.execute(
                DECR_STOCK,
                List.of(stockKey, userKey),
                userId.toString()
        );

        //判断是否符合条件
        if (execute == 0) {
            throw new BusinessException("您已领取过该优惠券");
        } else if (execute == -1) {
            throw new BusinessException("优惠券已被领取完");
        }

        // MQ异步更新库存，并绑定优惠券数据持久化
        CouponConsumerDTO couponConsumerDTO = new CouponConsumerDTO(
                userId,
                couponId,
                coupons.getValidityType(),
                coupons.getValidEndTime(),
                coupons.getDaysAfterClaim()
        );
        rabbitTemplate.convertAndSend("coupon.direct", "stock.decr", couponConsumerDTO);

    }

    /*
    //分布式锁加乐观锁完成优惠券的秒杀
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCouponToUser(Integer userId, Integer couponId) {//事务生效问题（事务未提交，锁先释放）
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

                if (!Objects.equals(CouponStatus.of(coupons.getStatus()), CouponStatus.ENABLED)) {
                    throw new BusinessException("该优惠券已不可领取");
                }

                if (startTime.isAfter(now)) {
                    throw new BusinessException("未到该优惠券领取时间");
                } else if (endTime.isBefore(now)) {
                    throw new BusinessException("该优惠券已过期，无法领取");
                }

                //3 更新库存（原子更新防止超卖）
                couponService.updateStock(couponId);

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
    }*/

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
        return couponsDTO.stream().map(dto -> getCouponBaseVO(dto.getType(), dto)).toList();
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
            throw new RuntimeException("传入的数据为空");
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
            case DISCOUNT_COUPON -> {
                DiscountCouponVO discountCouponVO = new DiscountCouponVO();
                BeanUtils.copyProperties(couponsDTO, discountCouponVO);
                return discountCouponVO;
            }
            case VOUCHER -> {
                VouchersVO vouchersVO = new VouchersVO();
                BeanUtils.copyProperties(couponsDTO, vouchersVO);
                return vouchersVO;
            }
            default -> throw new BusinessException("不存在的优惠券类型");
        }
    }

}
