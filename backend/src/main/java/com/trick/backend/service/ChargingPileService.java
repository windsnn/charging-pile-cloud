package com.trick.backend.service;

import com.trick.backend.common.result.PageResult;
import com.trick.backend.model.dto.ChargingPileAddAndUpdateDTO;
import com.trick.backend.model.dto.ChargingPileQueryDTO;
import com.trick.backend.model.pojo.ChargingPile;
import com.trick.backend.model.vo.ChargingPileVO;

import java.util.List;


public interface ChargingPileService {
    PageResult<ChargingPile> getChargingPilesByPage(ChargingPileQueryDTO chargingPileQueryDTO);

    ChargingPileVO getChargingPileById(Integer id);

    void addChargingPile(ChargingPileAddAndUpdateDTO chargingAddPileDTO);

    void updateChargingPile(Integer id, ChargingPileAddAndUpdateDTO chargingUpdatePileDTO);

    void deleteChargingPile(Integer id);

    List<ChargingPileVO> nearbyByRoadDistance(Double latitude, Double longitude);

}
