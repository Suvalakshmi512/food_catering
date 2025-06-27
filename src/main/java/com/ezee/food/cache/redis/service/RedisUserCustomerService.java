package com.ezee.food.cache.redis.service;

import com.ezee.food.cache.dto.UserCustomerCacheDTO;
import com.ezee.food.dto.UserCustomerDTO;

public interface RedisUserCustomerService {

    public UserCustomerDTO getUserCustomerFromCache(UserCustomerDTO inputDTO);
    public void putUserCustomerCache(String cacheKey, UserCustomerCacheDTO cacheDTO);


}
