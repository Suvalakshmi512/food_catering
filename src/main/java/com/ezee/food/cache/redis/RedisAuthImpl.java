package com.ezee.food.cache.redis;

import com.ezee.food.Exception.ErrorCode;
import com.ezee.food.Exception.ServiceException;
import com.ezee.food.cache.dto.AuthCacheDTO;
import com.ezee.food.cache.redis.service.RedisAuthService;
import com.ezee.food.dto.AuthResponseDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class RedisAuthImpl implements RedisAuthService {

    private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.cache.redis");

    private static final String AUTH_CACHE_NAME = "AUTH_CACHE";

    @Autowired
    private CacheManager cacheManager;

    public AuthResponseDTO getAuthByUsername(String username) {
        String cacheKey = "USERNAME_" + username;
        return getAuthFromCache(cacheKey, "username");
    }

    public AuthResponseDTO getAuthByToken(String token) {
        String cacheKey = "TOKEN_" + token;
        return getAuthFromCache(cacheKey, "token");
    }
    
    public AuthResponseDTO getAuthFromCache(String cacheKey, String type) {
    	AuthResponseDTO response = null;
        Cache cache = cacheManager.getCache(AUTH_CACHE_NAME);
        if (cache == null) {
            LOGGER.error("Auth cache not available");
            throw new ServiceException(ErrorCode.INVALID_TOKEN, "Auth cache not available");
        }


        ValueWrapper wrapper = cache.get(cacheKey);
        if (wrapper != null && wrapper.get() != null) {
            LOGGER.info("Cache HIT for key: {}", cacheKey);
            AuthCacheDTO cacheDTO = (AuthCacheDTO) wrapper.get();
            LOGGER.info("Cache content: {}", cacheDTO);
            response = bindAuthFromCacheObject(cacheDTO);
        } else {
            LOGGER.warn("Cache MISS for key: {}", cacheKey);
        }

        return response;
    }

        public void putAuth(AuthResponseDTO dto) {
        LOGGER.info("Attempting to put auth in cache for USERNAME_{} and TOKEN_{}", dto.getUsername(), dto.getAccessToken());
        Cache cache = cacheManager.getCache(AUTH_CACHE_NAME);
        if (cache == null) {
            LOGGER.error("AUTH_CACHE not found in CacheManager");
            return;
        }
        AuthCacheDTO cacheDTO = bindAuthToCacheObject(dto);
        cache.put("USERNAME_" + dto.getUsername(), cacheDTO);
        cache.put("TOKEN_" + dto.getAccessToken(), cacheDTO);
        LOGGER.info("Successfully cached auth for username USERNAME_{} and token TOKEN_{}", dto.getUsername(), dto.getAccessToken());
    }

    public void clearAuth(String key) {
        Cache cache = cacheManager.getCache(AUTH_CACHE_NAME);
        if (cache != null) {
            cache.evict("USERNAME_" + key);
            cache.evict("TOKEN_" + key);
            cache.evict(key);
            LOGGER.info("AUTH_CACHE: evicted key={}", key);
        }
    }

    private AuthCacheDTO bindAuthToCacheObject(AuthResponseDTO dto) {
        AuthCacheDTO cacheDTO = new AuthCacheDTO();
        cacheDTO.setUsername(dto.getUsername());
        cacheDTO.setAccessToken(dto.getAccessToken());
        return cacheDTO;
    }

    private AuthResponseDTO bindAuthFromCacheObject(AuthCacheDTO cacheDTO) {
        AuthResponseDTO dto = new AuthResponseDTO();
        dto.setUsername(cacheDTO.getUsername());
        dto.setAccessToken(cacheDTO.getAccessToken());
        return dto;
    }
}
