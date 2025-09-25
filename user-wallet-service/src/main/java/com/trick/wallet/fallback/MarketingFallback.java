package com.trick.wallet.fallback;

import com.trick.common.exception.BusinessException;
import com.trick.common.result.Result;
import com.trick.wallet.client.MarketingClient;
import com.trick.wallet.model.dto.CouponDTO;
import com.trick.wallet.model.vo.UpdateCouponForUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MarketingFallback implements FallbackFactory<MarketingClient> {
    @Override
    public MarketingClient create(Throwable cause) {
        return new MarketingClient() {
            @Override
            public Result<CouponDTO> getCouponById(Integer couponId) {
                log.error("调用getCouponById失败，原因：{}", cause.getMessage(), cause);
                throw new BusinessException("服务暂不可用");
            }

            @Override
            public Result<?> updateCouponInformationForUser(Integer couponId, UpdateCouponForUserVO updateCouponForUserVO) {
                log.error("调用updateCouponInformationForUser失败，原因：{}", cause.getMessage(), cause);
                throw new BusinessException("服务暂不可用");
            }
        };
    }
}
