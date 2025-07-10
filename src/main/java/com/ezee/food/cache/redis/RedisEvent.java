package com.ezee.food.cache.redis;

import com.ezee.food.cache.dto.EventCacheDTO;
import com.ezee.food.cache.redis.service.RedisEventService;
import com.ezee.food.dao.EventDAO;
import com.ezee.food.dto.EventDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class RedisEvent implements RedisEventService {

	private static final String EVENT_CACHE_NAME = "EVENT_CACHE";
	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private EventDAO eventDAO;

	public EventDTO getEventFromCache(EventDTO inputDTO) {
		EventDTO response = null;
		if (inputDTO == null || inputDTO.getId() == 0) {
			LOGGER.info("EventDTO or id is 0");
		}

		String cacheKey = "EVENT_" + inputDTO.getId();
		Cache cache = cacheManager.getCache(EVENT_CACHE_NAME);

		if (cache != null) {
			ValueWrapper wrapper = cache.get(cacheKey);
			if (wrapper != null && wrapper.get() != null) {
				EventCacheDTO cacheDTO = (EventCacheDTO) wrapper.get();
				response = convertToDTO(cacheDTO);
			}
		}
		if(response == null) {
		response = eventDAO.getEvent(inputDTO);
		}

		if (response != null && response.getCode() != null) {
			EventCacheDTO cacheDTO = convertToCacheDTO(response);
			putEventCache(cacheKey, cacheDTO);
		}

		return response;
	}

	public void putEventCache(String cacheKey, EventCacheDTO cacheDTO) {
		Cache cache = cacheManager.getCache(EVENT_CACHE_NAME);
		if (cache != null) {
			cache.put(cacheKey, cacheDTO);
		}
	}

	private EventCacheDTO convertToCacheDTO(EventDTO dto) {
		EventCacheDTO cacheDTO = new EventCacheDTO();
		cacheDTO.setId(dto.getId());
		cacheDTO.setCode(dto.getCode());
		cacheDTO.setName(dto.getName());
		cacheDTO.setActiveFlag(dto.getActiveFlag());
		cacheDTO.setCustomerDTO(dto.getCustomerDTO());
		cacheDTO.setEventDate(dto.getEventDate());
		cacheDTO.setEventTime(dto.getEventTime());
		cacheDTO.setVenue(dto.getVenue());
		cacheDTO.setGuestCount(dto.getGuestCount());
		return cacheDTO;
	}

	private EventDTO convertToDTO(EventCacheDTO cacheDTO) {
		EventDTO dto = new EventDTO();
		dto.setId(cacheDTO.getId());
		dto.setCode(cacheDTO.getCode());
		dto.setName(cacheDTO.getName());
		dto.setActiveFlag(cacheDTO.getActiveFlag());
		dto.setCustomerDTO(cacheDTO.getCustomerDTO());
		dto.setEventDate(cacheDTO.getEventDate());
		dto.setEventTime(cacheDTO.getEventTime());
		dto.setVenue(cacheDTO.getVenue());
		dto.setGuestCount(cacheDTO.getGuestCount());
		return dto;
	}
}
