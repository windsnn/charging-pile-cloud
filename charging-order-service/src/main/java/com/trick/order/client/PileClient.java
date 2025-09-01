package com.trick.order.client;

import com.trick.common.config.FeignConfig;
import com.trick.common.result.Result;
import com.trick.order.model.vo.ChargingPileVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "pile-service", configuration = FeignConfig.class)
public interface PileClient {
    @GetMapping("/wx/piles/{id}")
    Result<ChargingPileVO> getChargingPile(@PathVariable("id") Integer pileId);

    @PutMapping("/super/state")
    Result<?> setState(@RequestParam("id") Integer pileId, @RequestParam("state") Integer state);

}
