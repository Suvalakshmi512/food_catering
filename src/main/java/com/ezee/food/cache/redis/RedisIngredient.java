package com.ezee.food.cache.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.stereotype.Service;

import com.ezee.food.cache.dto.IngredientCacheDTO;
import com.ezee.food.cache.redis.service.RedisIngredientService;
import com.ezee.food.dao.IngredientDAO;
import com.ezee.food.dto.IngredientDTO;

import org.springframework.cache.CacheManager;

@Service
public class RedisIngredient implements RedisIngredientService {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private IngredientDAO ingredientDAO;

	private static final String INGREDIENT_CACHE_NAME = "INGREDIENT_CACHE";

	public IngredientDTO getIngredientFromCache(IngredientDTO ingredientDTO) {
		IngredientDTO response = null;
		String cacheKey = "ING_" + ingredientDTO.getCode();

		ValueWrapper wrapper = cacheManager.getCache(INGREDIENT_CACHE_NAME).get(cacheKey);
		if (wrapper != null && wrapper.get() != null) {
			IngredientCacheDTO cacheDTO = (IngredientCacheDTO) wrapper.get();
			response = convertToIngredientDTO(cacheDTO);
		}
		if(response == null) {
		response = ingredientDAO.getIngredient(ingredientDTO);
		}
		if (response != null && response.getCode() != null) {
			IngredientCacheDTO cacheDTO = convertToCacheDTO(response);
			putIngredientCache("ING_" + response.getCode(), cacheDTO);
		}

		return response;
	}

	public void putIngredientCache(String cacheKey, IngredientCacheDTO cacheDTO) {
		Cache cache = cacheManager.getCache(INGREDIENT_CACHE_NAME);
		if (cache != null) {
			cache.put(cacheKey, cacheDTO);
		}
	}

	private IngredientCacheDTO convertToCacheDTO(IngredientDTO dto) {
		IngredientCacheDTO cacheDTO = new IngredientCacheDTO();
		cacheDTO.setId(dto.getId());
		cacheDTO.setCode(dto.getCode());
		cacheDTO.setName(dto.getName());
		cacheDTO.setUnit(dto.getUnit());
		cacheDTO.setUnitQuantity(dto.getUnitQuantity());
		cacheDTO.setUnitCost(dto.getUnitCost());
		cacheDTO.setActiveFlag(dto.getActiveFlag());
		cacheDTO.setTaxDTO(dto.getTaxDTO());
		return cacheDTO;
	}

	private IngredientDTO convertToIngredientDTO(IngredientCacheDTO cacheDTO) {
		IngredientDTO dto = new IngredientDTO();
		dto.setId(cacheDTO.getId());
		dto.setCode(cacheDTO.getCode());
		dto.setName(cacheDTO.getName());
		dto.setUnit(cacheDTO.getUnit());
		dto.setUnitQuantity(cacheDTO.getUnitQuantity());
		dto.setUnitCost(cacheDTO.getUnitCost());
		dto.setActiveFlag(cacheDTO.getActiveFlag());
		dto.setTaxDTO(cacheDTO.getTaxDTO());
		return dto;
	}
}