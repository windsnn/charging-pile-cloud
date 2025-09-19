package com.trick.wallet.client;

import com.trick.common.config.FeignConfig;
import com.trick.common.result.Result;
import com.trick.wallet.model.dto.CouponDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PutMapping("wx/marketing/coupons/status/{id}")
    Result<Integer> setTheCouponStatus(@PathVariable("id") Integer couponId, @RequestParam("status") Integer status);
}
