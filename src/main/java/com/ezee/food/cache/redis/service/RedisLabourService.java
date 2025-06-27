package com.ezee.food.cache.redis.service;

import com.ezee.food.cache.dto.LabourCacheDTO;
import com.ezee.food.dto.LabourDTO;

public interface RedisLabourService {
    public LabourDTO getLabourFromCache(LabourDTO inputDTO);
    public void putLabourCache(String cacheKey, LabourCacheDTO cacheDTO);

}
