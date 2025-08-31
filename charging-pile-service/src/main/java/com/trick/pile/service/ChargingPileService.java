package com.trick.pile.service;

import com.trick.common.result.PageResult;
import com.trick.pile.model.dto.ChargingPileAddAndUpdateDTO;
import com.trick.pile.model.dto.ChargingPileQueryDTO;
import com.trick.pile.model.pojo.ChargingPile;
import com.trick.pile.model.vo.ChargingPileVO;

import java.util.List;


public interface ChargingPileService {
    PageResult<ChargingPile> getChargingPilesByPage(ChargingPileQueryDTO chargingPileQueryDTO);

    ChargingPileVO getChargingPileById(Integer id);

    void addChargingPile(ChargingPileAddAndUpdateDTO chargingAddPileDTO);

    void updateChargingPile(Integer id, ChargingPileAddAndUpdateDTO chargingUpdatePileDTO);

    void deleteChargingPile(Integer id);

    List<ChargingPileVO> nearbyByRoadDistance(Double latitude, Double longitude);

}
