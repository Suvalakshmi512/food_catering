package com.ezee.food.cache.redis;

import com.ezee.food.cache.dto.TaxCacheDTO;
import com.ezee.food.cache.redis.service.RedisTaxService;
import com.ezee.food.dao.TaxDAO;
import com.ezee.food.dto.TaxDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class RedisTax implements RedisTaxService {

	private static final String TAX_CACHE_NAME = "TAX_CACHE";
	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private TaxDAO taxDAO;

	public TaxDTO getTaxFromCache(TaxDTO inputDTO) {
		TaxDTO response = null;
		if (inputDTO == null || inputDTO.getId() == 0) {
			LOGGER.info("TaxDTO or id is null");
		}

		String cacheKey = "TAX_" + inputDTO.getId();
		Cache cache = cacheManager.getCache(TAX_CACHE_NAME);

		if (cache != null) {
			ValueWrapper wrapper = cache.get(cacheKey);
			if (wrapper != null && wrapper.get() != null) {
				TaxCacheDTO cacheDTO = (TaxCacheDTO) wrapper.get();
				response = convertToDTO(cacheDTO);
			}
		}
		if(response == null) {
		response = taxDAO.getTax(inputDTO);
		}
		if (response != null && response.getCode() != null) {
			TaxCacheDTO cacheDTO = convertToCacheDTO(response);
			putTaxCache(cacheKey, cacheDTO);
		}

		return response;
	}

	public void putTaxCache(String cacheKey, TaxCacheDTO cacheDTO) {
		Cache cache = cacheManager.getCache(TAX_CACHE_NAME);
		if (cache != null) {
			cache.put(cacheKey, cacheDTO);
		}
	}

	private TaxCacheDTO convertToCacheDTO(TaxDTO dto) {
		TaxCacheDTO cacheDTO = new TaxCacheDTO();
		cacheDTO.setId(dto.getId());
		cacheDTO.setCode(dto.getCode());
		cacheDTO.setDescription(dto.getDescription());
		cacheDTO.setRatePercentage(dto.getRatePercentage());
		cacheDTO.setActiveFlag(dto.getActiveFlag());
		return cacheDTO;
	}

	private TaxDTO convertToDTO(TaxCacheDTO cacheDTO) {
		TaxDTO dto = new TaxDTO();
		dto.setId(cacheDTO.getId());
		dto.setCode(cacheDTO.getCode());
		dto.setDescription(cacheDTO.getDescription());
		dto.setRatePercentage(cacheDTO.getRatePercentage());
		dto.setActiveFlag(cacheDTO.getActiveFlag());
		return dto;
	}
}
