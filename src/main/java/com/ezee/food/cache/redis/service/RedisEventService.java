package com.ezee.food.cache.redis.service;

import com.ezee.food.cache.dto.EventCacheDTO;
import com.ezee.food.dto.EventDTO;

public interface RedisEventService {
    public EventDTO getEventFromCache(EventDTO inputDTO);
    public void putEventCache(String cacheKey, EventCacheDTO cacheDTO);

}
