package com.trick.user.client;

import com.trick.common.result.Result;
import com.trick.user.model.vo.ChargingPileVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("pile-service")
public interface PileClient {

    @GetMapping("/wx/piles/{id}")
    Result<ChargingPileVO> getChargingPileById(@PathVariable("id") Integer pileId);
}
