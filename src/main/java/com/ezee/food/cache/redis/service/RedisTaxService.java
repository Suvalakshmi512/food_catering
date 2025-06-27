package com.ezee.food.cache.redis.service;

import com.ezee.food.cache.dto.TaxCacheDTO;
import com.ezee.food.dto.TaxDTO;

public interface RedisTaxService {
    public TaxDTO getTaxFromCache(TaxDTO inputDTO);
    public void putTaxCache(String cacheKey, TaxCacheDTO cacheDTO);

}
