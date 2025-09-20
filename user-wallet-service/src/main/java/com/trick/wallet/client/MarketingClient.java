package com.trick.wallet.client;

import com.trick.common.config.FeignConfig;
import com.trick.common.result.Result;
import com.trick.wallet.model.dto.CouponDTO;
import com.trick.wallet.model.vo.UpdateCouponForUserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@FeignClient(name = "marketing-service", configuration = FeignConfig.class)
public interface MarketingClient {

    /**
     * 获取优惠券信息，顺便判断是否拥有优惠券
     *
     * @param couponId 优惠券ID
     * @return 优惠券数据
     */
    @GetMapping("/wx/marketing/coupons/{id}")
    Result<CouponDTO> getCouponById(@PathVariable("id") Integer couponId);

    /**
     * 更新用户优惠券信息
     *
     * @param couponId 要更新的用户优惠券ID
     * @param updateCouponForUserVO 要更新的数据
     * @return 统一
     */
    @PostMapping("/internal/wx/marketing/coupons/{id}")
    Result<?> updateCouponInformationForUser(@PathVariable("id") Integer couponId,
                                             @RequestBody UpdateCouponForUserVO updateCouponForUserVO);
}
