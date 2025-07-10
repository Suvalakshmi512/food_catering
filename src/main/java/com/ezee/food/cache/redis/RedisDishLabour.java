
package com.ezee.food.cache.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.ezee.food.cache.dto.DishLabourCacheDTO;
import com.ezee.food.cache.redis.service.RedisDishLabourService;
import com.ezee.food.dao.DishLabourDAO;
import com.ezee.food.dto.DishLabourDTO;

@Service
public class RedisDishLabour implements RedisDishLabourService {

	private static final String DISH_LABOUR_CACHE_NAME = "DISH_LABOUR_CACHE";

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private DishLabourDAO dishLabourDAO;

	public DishLabourDTO getDishLabourFromCache(DishLabourDTO inputDTO) {
		DishLabourDTO response = null;
		String cacheKey = "DL_" + inputDTO.getCode();
		Cache cache = cacheManager.getCache(DISH_LABOUR_CACHE_NAME);

		if (cache != null) {
			ValueWrapper wrapper = cache.get(cacheKey);
			if (wrapper != null && wrapper.get() != null) {
				DishLabourCacheDTO cacheDTO = (DishLabourCacheDTO) wrapper.get();
				response = convertToDTO(cacheDTO);
			}
		}
		if(response == null) {
		response = dishLabourDAO.getDishLabour(inputDTO);
		}
		if (response != null && response.getCode() != null) {
			DishLabourCacheDTO cacheDTO = convertToCacheDTO(response);
			putDishLabourCache(cacheKey, cacheDTO);
		}

		return response;
	}

	public void putDishLabourCache(String cacheKey, DishLabourCacheDTO cacheDTO) {
		Cache cache = cacheManager.getCache(DISH_LABOUR_CACHE_NAME);
		if (cache != null) {
			cache.put(cacheKey, cacheDTO);
		}
	}

	private DishLabourCacheDTO convertToCacheDTO(DishLabourDTO dto) {
		DishLabourCacheDTO cacheDTO = new DishLabourCacheDTO();
		cacheDTO.setId(dto.getId());
		cacheDTO.setCode(dto.getCode());
		cacheDTO.setActiveFlag(dto.getActiveFlag());
		cacheDTO.setLabourDTO(dto.getLabourDTO());
		cacheDTO.setDishDTO(dto.getDishDTO());
		cacheDTO.setHoursRequired(dto.getHoursRequired());
		return cacheDTO;
	}

	private DishLabourDTO convertToDTO(DishLabourCacheDTO cacheDTO) {
		DishLabourDTO dto = new DishLabourDTO();
		dto.setId(cacheDTO.getId());
		dto.setCode(cacheDTO.getCode());
		dto.setActiveFlag(cacheDTO.getActiveFlag());
		dto.setLabourDTO(cacheDTO.getLabourDTO());
		dto.setDishDTO(cacheDTO.getDishDTO());
		dto.setHoursRequired(cacheDTO.getHoursRequired());
		return dto;
	}
}
