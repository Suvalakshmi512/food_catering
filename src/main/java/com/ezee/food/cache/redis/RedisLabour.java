package com.ezee.food.cache.redis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.ezee.food.cache.dto.LabourCacheDTO;
import com.ezee.food.cache.redis.service.RedisLabourService;
import com.ezee.food.dao.LabourDAO;
import com.ezee.food.dto.LabourDTO;

@Service
public class RedisLabour implements RedisLabourService {

	private static final String LABOUR_CACHE_NAME = "LABOUR_CACHE";

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private LabourDAO labourDAO;
	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	public LabourDTO getLabourFromCache(LabourDTO inputDTO) {
		if (inputDTO == null || inputDTO.getId() == 0) {
			LOGGER.info("The labourDTO Id is null");
		}
		LabourDTO response = null;
		String cacheKey = "LAB_" + inputDTO.getId();
		Cache cache = cacheManager.getCache(LABOUR_CACHE_NAME);

		if (cache != null) {
			ValueWrapper wrapper = cache.get(cacheKey);
			if (wrapper != null && wrapper.get() != null) {
				LabourCacheDTO cacheDTO = (LabourCacheDTO) wrapper.get();
				response = convertToDTO(cacheDTO);
			}
		}

		response = labourDAO.getLabour(inputDTO);
		if (response != null && response.getCode() != null) {
			LabourCacheDTO cacheDTO = convertToCacheDTO(response);
			putLabourCache(cacheKey, cacheDTO);
		}

		return response;
	}

	public void putLabourCache(String cacheKey, LabourCacheDTO cacheDTO) {
		Cache cache = cacheManager.getCache(LABOUR_CACHE_NAME);
		if (cache != null) {
			cache.put(cacheKey, cacheDTO);
		}
	}

	private LabourCacheDTO convertToCacheDTO(LabourDTO dto) {
		LabourCacheDTO cacheDTO = new LabourCacheDTO();
		cacheDTO.setCode(dto.getCode());
		cacheDTO.setName(dto.getName());
		cacheDTO.setRoleName(dto.getRoleName());
		cacheDTO.setHourslySalary(dto.getHourslySalary());
		cacheDTO.setSpecialization(dto.getSpecialization());
		cacheDTO.setActiveFlag(dto.getActiveFlag());
		return cacheDTO;
	}

	private LabourDTO convertToDTO(LabourCacheDTO cacheDTO) {
		LabourDTO dto = new LabourDTO();
		dto.setCode(cacheDTO.getCode());
		dto.setName(cacheDTO.getName());
		dto.setRoleName(cacheDTO.getRoleName());
		dto.setHourslySalary(cacheDTO.getHourslySalary());
		dto.setSpecialization(cacheDTO.getSpecialization());
		dto.setActiveFlag(cacheDTO.getActiveFlag());
		return dto;
	}
}
