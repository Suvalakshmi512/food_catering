package com.ezee.food.cache.redis;

import com.ezee.food.cache.dto.UserCustomerCacheDTO;
import com.ezee.food.cache.redis.service.RedisUserCustomerService;
import com.ezee.food.dao.UserCustomerDAO;
import com.ezee.food.dto.UserCustomerDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class RedisUserCustomer implements RedisUserCustomerService {

	private static final String USER_CUSTOMER_CACHE_NAME = "USER_CUSTOMER_CACHE";
	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private UserCustomerDAO userCustomerDAO;

	public UserCustomerDTO getUserCustomerFromCache(UserCustomerDTO inputDTO) {
		UserCustomerDTO response = null;
		if (inputDTO == null || inputDTO.getId() == 0) {
			LOGGER.info("UserCustomerDTO or id is 0");
		}

		String cacheKey = "USER_CUST_" + inputDTO.getId();
		Cache cache = cacheManager.getCache(USER_CUSTOMER_CACHE_NAME);

		if (cache != null) {
			ValueWrapper wrapper = cache.get(cacheKey);
			if (wrapper != null && wrapper.get() != null) {
				UserCustomerCacheDTO cacheDTO = (UserCustomerCacheDTO) wrapper.get();
				response = convertToDTO(cacheDTO);
			}
		}
		if(response == null) {
		response = userCustomerDAO.getCustomer(inputDTO);
		}
		if (response != null && response.getCode() != null) {
			UserCustomerCacheDTO cacheDTO = convertToCacheDTO(response);
			putUserCustomerCache(cacheKey, cacheDTO);
		}

		return response;
	}

	public void putUserCustomerCache(String cacheKey, UserCustomerCacheDTO cacheDTO) {
		Cache cache = cacheManager.getCache(USER_CUSTOMER_CACHE_NAME);
		if (cache != null) {
			cache.put(cacheKey, cacheDTO);
		}
	}

	private UserCustomerCacheDTO convertToCacheDTO(UserCustomerDTO dto) {
		UserCustomerCacheDTO cacheDTO = new UserCustomerCacheDTO();
		cacheDTO.setId(dto.getId());
		cacheDTO.setCode(dto.getCode());
		cacheDTO.setName(dto.getName());
		cacheDTO.setEmail(dto.getEmail());
		cacheDTO.setMobile(dto.getMobile());
		cacheDTO.setActiveFlag(dto.getActiveFlag());
		return cacheDTO;
	}

	private UserCustomerDTO convertToDTO(UserCustomerCacheDTO cacheDTO) {
		UserCustomerDTO dto = new UserCustomerDTO();
		dto.setId(cacheDTO.getId());
		dto.setCode(cacheDTO.getCode());
		dto.setName(cacheDTO.getName());
		dto.setEmail(cacheDTO.getEmail());
		dto.setMobile(cacheDTO.getMobile());
		dto.setActiveFlag(cacheDTO.getActiveFlag());
		return dto;
	}
}
