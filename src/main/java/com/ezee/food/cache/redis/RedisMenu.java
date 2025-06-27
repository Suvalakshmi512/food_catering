package com.ezee.food.cache.redis;

import com.ezee.food.cache.dto.MenuCacheDTO;
import com.ezee.food.cache.redis.service.RedisMenuService;
import com.ezee.food.dao.MenuDAO;
import com.ezee.food.dto.MenuDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class RedisMenu implements RedisMenuService{

	private static final String MENU_CACHE_NAME = "MENU_CACHE";
	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private MenuDAO menuDAO;

	public MenuDTO getMenuFromCache(MenuDTO inputDTO) {
		MenuDTO response = null;
		if (inputDTO == null || inputDTO.getId() == 0) {
			LOGGER.info("MenuDTO or id is 0");
		}

		String cacheKey = "MENU_" + inputDTO.getId();
		Cache cache = cacheManager.getCache(MENU_CACHE_NAME);

		if (cache != null) {
			ValueWrapper wrapper = cache.get(cacheKey);
			if (wrapper != null && wrapper.get() != null) {
				MenuCacheDTO cacheDTO = (MenuCacheDTO) wrapper.get();
				response = convertToDTO(cacheDTO);
			}
		}

		response = menuDAO.getMenu(inputDTO);
		if (response != null && response.getCode() != null) {
			MenuCacheDTO cacheDTO = convertToCacheDTO(response);
			putMenuCache(cacheKey, cacheDTO);
		}

		return response;
	}

	public void putMenuCache(String cacheKey, MenuCacheDTO cacheDTO) {
		Cache cache = cacheManager.getCache(MENU_CACHE_NAME);
		if (cache != null) {
			cache.put(cacheKey, cacheDTO);
		}
	}

	private MenuCacheDTO convertToCacheDTO(MenuDTO dto) {
		MenuCacheDTO cacheDTO = new MenuCacheDTO();
		cacheDTO.setId(dto.getId());
		cacheDTO.setCode(dto.getCode());
		cacheDTO.setName(dto.getName());
		cacheDTO.setActiveFlag(dto.getActiveFlag());
		cacheDTO.setDishListDTO(dto.getDishListDTO());
		cacheDTO.setPrice(dto.getPrice());
		return cacheDTO;
	}

	private MenuDTO convertToDTO(MenuCacheDTO cacheDTO) {
		MenuDTO dto = new MenuDTO();
		dto.setId(cacheDTO.getId());
		dto.setCode(cacheDTO.getCode());
		dto.setName(cacheDTO.getName());
		dto.setActiveFlag(cacheDTO.getActiveFlag());
		dto.setDishListDTO(cacheDTO.getDishListDTO());
		dto.setPrice(cacheDTO.getPrice());
		return dto;
	}

	
}
