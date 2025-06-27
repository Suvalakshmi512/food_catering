package com.ezee.food.cache.redis.service;

import com.ezee.food.cache.dto.IngredientCacheDTO;
import com.ezee.food.dto.IngredientDTO;

public interface RedisIngredientService {
    public IngredientDTO getIngredientFromCache(IngredientDTO ingredientDTO);
    public void putIngredientCache(String cacheKey, IngredientCacheDTO cacheDTO);


}
