package com.ezee.food.cache.redis.service;

import com.ezee.food.cache.dto.DishCacheDTO;
import com.ezee.food.dto.DishDTO;

public interface RedisDishService {
	 public DishDTO getDishFromCache(DishDTO inputDTO);
	 public void putDishCache(String cacheKey, DishCacheDTO cacheDTO);
}
