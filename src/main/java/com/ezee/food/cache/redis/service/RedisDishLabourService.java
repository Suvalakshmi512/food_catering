package com.ezee.food.cache.redis.service;

import com.ezee.food.cache.dto.DishLabourCacheDTO;
import com.ezee.food.dto.DishLabourDTO;

public interface RedisDishLabourService {
    public DishLabourDTO getDishLabourFromCache(DishLabourDTO inputDTO);
    public void putDishLabourCache(String cacheKey, DishLabourCacheDTO cacheDTO);


}
