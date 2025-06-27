package com.ezee.food.cache.redis;

import com.ezee.food.cache.dto.DishCacheDTO;
import com.ezee.food.cache.redis.service.RedisDishService;
import com.ezee.food.dao.DishDAO;
import com.ezee.food.dto.DishDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class RedisDish implements RedisDishService{

    private static final String DISH_CACHE_NAME = "DISH_CACHE";
    private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DishDAO dishDAO;

    public DishDTO getDishFromCache(DishDTO inputDTO) {
    	DishDTO response = null;
        if (inputDTO == null || inputDTO.getId() == 0) {
            LOGGER.info("DishDTO or id is 0");
        }

        String cacheKey = "DISH_" + inputDTO.getId();
        Cache cache = cacheManager.getCache(DISH_CACHE_NAME);

        if (cache != null) {
            ValueWrapper wrapper = cache.get(cacheKey);
            if (wrapper != null && wrapper.get() != null) {
                DishCacheDTO cacheDTO = (DishCacheDTO) wrapper.get();
                response = convertToDTO(cacheDTO);
            }
        }

        response = dishDAO.getDish(inputDTO);
        if (response != null && response.getCode() != null) {
            DishCacheDTO cacheDTO = convertToCacheDTO(response);
            putDishCache(cacheKey, cacheDTO);
        }

        return response;
    }

    public void putDishCache(String cacheKey, DishCacheDTO cacheDTO) {
        Cache cache = cacheManager.getCache(DISH_CACHE_NAME);
        if (cache != null) {
            cache.put(cacheKey, cacheDTO);
        }
    }

    private DishCacheDTO convertToCacheDTO(DishDTO dto) {
        DishCacheDTO cacheDTO = new DishCacheDTO();
        cacheDTO.setId(dto.getId());
        cacheDTO.setCode(dto.getCode());
        cacheDTO.setName(dto.getName());
        cacheDTO.setActiveFlag(dto.getActiveFlag());
        cacheDTO.setDescription(dto.getDescription());
        cacheDTO.setTimeToMake(dto.getTimeToMake());
        cacheDTO.setVegType(dto.getVegType());
        cacheDTO.setMinAvailableQuantity(dto.getMinAvailableQuantity());
        cacheDTO.setServingSize(dto.getServingSize());
        cacheDTO.setMarginProfit(dto.getMarginProfit());
        cacheDTO.setPrice(dto.getPrice());
        cacheDTO.setTaxDTO(dto.getTaxDTO());
        cacheDTO.setDishIngredientList(dto.getDishIngredientList());
        cacheDTO.setDishLabourList(dto.getDishLabourList());
        return cacheDTO;
    }

    private DishDTO convertToDTO(DishCacheDTO cacheDTO) {
        DishDTO dto = new DishDTO();
        dto.setId(cacheDTO.getId());
        dto.setCode(cacheDTO.getCode());
        dto.setName(cacheDTO.getName());
        dto.setActiveFlag(cacheDTO.getActiveFlag());
        dto.setDescription(cacheDTO.getDescription());
        dto.setTimeToMake(cacheDTO.getTimeToMake());
        dto.setVegType(cacheDTO.getVegType());
        dto.setMinAvailableQuantity(cacheDTO.getMinAvailableQuantity());
        dto.setServingSize(cacheDTO.getServingSize());
        dto.setMarginProfit(cacheDTO.getMarginProfit());
        dto.setPrice(cacheDTO.getPrice());
        dto.setTaxDTO(cacheDTO.getTaxDTO());
        dto.setDishIngredientList(cacheDTO.getDishIngredientList());
        dto.setDishLabourList(cacheDTO.getDishLabourList());
        return dto;
    }
}
