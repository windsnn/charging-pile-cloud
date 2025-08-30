package com.trick.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.trick.backend.common.result.PageResult;
import com.trick.backend.mapper.ChargingPileMapper;
import com.trick.backend.model.dto.ChargingPileAddAndUpdateDTO;
import com.trick.backend.model.dto.ChargingPileQueryDTO;
import com.trick.backend.model.pojo.ChargingPile;
import com.trick.backend.model.vo.ChargingPileVO;
import com.trick.backend.service.ChargingPileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChargingPileServiceImpl implements ChargingPileService {
    private static final String PILE_GEO_KEY = "piles:geo";
    @Value("${tx.apiKey}")
    private String apiKey;
    @Value("${tx.apiUrl}")
    private String apiUrl;
    @Autowired
    private ChargingPileMapper chargingPileMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //分页条件查询
    @Override
    public PageResult<ChargingPile> getChargingPilesByPage(ChargingPileQueryDTO chargingPileQueryDTO) {
        PageHelper.startPage(chargingPileQueryDTO.getPageNum(), chargingPileQueryDTO.getPageSize());
        List<ChargingPile> list = chargingPileMapper.getAllChargingPiles(chargingPileQueryDTO);
        PageInfo<ChargingPile> pageInfo = new PageInfo<>(list);

        List<ChargingPile> records = pageInfo.getList();
        long total = pageInfo.getTotal();

        return new PageResult<>(total, records);
    }

    // 根据ID查询
    @Override
    @Cacheable(value = "pile", key = "#id")
    public ChargingPileVO getChargingPileById(Integer id) {
        ChargingPileVO chargingPileVO = new ChargingPileVO();
        ChargingPile chargingPile = chargingPileMapper.getChargingPileById(id);

        BeanUtils.copyProperties(chargingPile, chargingPileVO);

        return chargingPileVO;
    }

    //添加充电桩
    @Override
    public void addChargingPile(ChargingPileAddAndUpdateDTO dto) {
        dto.setCreateTime(LocalDateTime.now());
        dto.setUpdateTime(LocalDateTime.now());
        chargingPileMapper.addChargingPile(dto);
        // 同步地理位置到Redis GEO
        redisTemplate.opsForGeo().add(
                PILE_GEO_KEY,
                new Point(dto.getLongitude(), dto.getLatitude()),
                dto.getId().toString()
        );
    }

    //更新充电桩
    @Override
    @CacheEvict(value = "pile", key = "#id")
    public void updateChargingPile(Integer id, ChargingPileAddAndUpdateDTO chargingUpdatePileDTO) {
        chargingUpdatePileDTO.setId(id);
        chargingUpdatePileDTO.setUpdateTime(LocalDateTime.now());

        chargingPileMapper.updateChargingPile(chargingUpdatePileDTO);

        // 同步地理位置到Redis GEO
        redisTemplate.opsForGeo().remove(PILE_GEO_KEY, chargingUpdatePileDTO.getId().toString());
        redisTemplate.opsForGeo().add(
                PILE_GEO_KEY,
                new Point(chargingUpdatePileDTO.getLongitude(), chargingUpdatePileDTO.getLatitude()),
                chargingUpdatePileDTO.getId().toString()
        );
    }

    //删除充电桩
    @Override
    @CacheEvict(value = "pile", key = "#id")
    public void deleteChargingPile(Integer id) {
        chargingPileMapper.deleteCharging(id);
        // 删除该充电桩的 Redis GEO
        redisTemplate.opsForGeo().remove(PILE_GEO_KEY, String.valueOf(id));
    }

    //获取距离当前位置10公里以内的充电桩
    //寻找最近的5个充电桩
    @Override
    public List<ChargingPileVO> nearbyByRoadDistance(Double latitude, Double longitude) {
        // 定义搜索半径和候选数量
        double maxStraightDistanceInMeters = 10000.0; // 10公里
        int maxCandidates = 10; // 最多找10个候选桩

        // 使用 Redis GEO 查询10公里内的候选充电桩，按距离升序排序，最多返回10个
        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
        Point userLocation = new Point(longitude, latitude);
        Distance radius = new Distance(maxStraightDistanceInMeters / 1000, Metrics.KILOMETERS);

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance() // 结果中包含距离
                .sortAscending()   // 按距离升序排序
                .limit(maxCandidates); // 限制返回数量

        // 执行GEORADIUS查询
        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = geoOps.radius(PILE_GEO_KEY, new Circle(userLocation, radius), args);
        if (geoResults == null || geoResults.getContent().isEmpty()) {
            return Collections.emptyList(); // 附近没有充电桩
        }

        // 提取充电桩ID列表
        List<Integer> pileIds = geoResults.getContent().stream()
                .map(result -> Integer.valueOf(result.getContent().getName()))
                .collect(Collectors.toList());


        // 根据ID列表，批量从数据库查询充电桩的完整信息
        List<ChargingPile> pilesFromDb = chargingPileMapper.findByIds(pileIds);
        if (pilesFromDb.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, ChargingPile> pileMap = pilesFromDb.stream()
                .collect(Collectors.toMap(ChargingPile::getId, pile -> pile));

        // 调用腾讯地图 API 获取道路距离
        List<ChargingPileVO> candidates = new ArrayList<>();
        for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : geoResults.getContent()) {
            Integer pileId = Integer.valueOf(result.getContent().getName());
            ChargingPile pile = pileMap.get(pileId);

            if (pile != null) {
                ChargingPileVO vo = new ChargingPileVO();
                BeanUtils.copyProperties(pile, vo);

                try {
                    String url = String.format(apiUrl, latitude, longitude, pile.getLatitude(), pile.getLongitude(), apiKey);
                    String jsonResponse = restTemplate.getForObject(url, String.class);
                    JsonNode jsonNode = objectMapper.readTree(jsonResponse);
                    if (jsonNode.get("status").asInt() == 0) {
                        double roadDistance = jsonNode.get("result").get("routes").get(0).get("distance").asDouble();
                        vo.setRoadDistance(roadDistance);
                    } else {
                        vo.setRoadDistance(Double.MAX_VALUE);
                    }
                } catch (Exception e) {
                    vo.setRoadDistance(Double.MAX_VALUE);
                    log.error("调用腾讯地图 API 获取道路距离失败 for pileId: {}", pileId, e);
                }
                candidates.add(vo);
            }
        }

        // 根据道路距离排序 + 取前5个返回
        return candidates.stream()
                .filter(p -> p.getRoadDistance() != null && p.getRoadDistance() < Double.MAX_VALUE)
                .sorted(Comparator.comparingDouble(ChargingPileVO::getRoadDistance))
                .limit(5)
                .collect(Collectors.toList());
    }

}
