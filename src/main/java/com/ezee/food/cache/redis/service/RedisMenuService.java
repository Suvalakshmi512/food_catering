package com.ezee.food.cache.redis.service;

import com.ezee.food.cache.dto.MenuCacheDTO;
import com.ezee.food.dto.MenuDTO;

public interface RedisMenuService {
	public MenuDTO getMenuFromCache(MenuDTO inputDTO);
	public void putMenuCache(String cacheKey, MenuCacheDTO cacheDTO);


}
