package com.ezee.food.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezee.food.Exception.ErrorCode;
import com.ezee.food.Exception.ServiceException;
import com.ezee.food.cache.redis.service.RedisAuthService;
import com.ezee.food.dto.AuthDTO;
import com.ezee.food.dto.AuthResponseDTO;
import com.ezee.food.service.AuthService;
import com.ezee.food.util.HttpClientHandler;

@Service
public class AuthImpl implements AuthService {

	@Autowired
	private HttpClientHandler httpClientHandler;

	@Autowired
	private RedisAuthService cacheService;

	private static final String AUTH_URL = "https://dummyjson.com/auth/login";

	public static final Logger LOGGER = LogManager.getLogger("com.ezee.product.service");

	public AuthResponseDTO authToken(AuthDTO authDTO) {
		String tokenKey = authDTO.getUsername();
		AuthResponseDTO responseDTO = null;
		try {
			AuthResponseDTO byKey = cacheService.getAuthByUsername(tokenKey);
			if (byKey != null) {
				responseDTO = byKey;
			} else {
				AuthResponseDTO dto = httpClientHandler.authenticateViaThirdPartyPost(authDTO, AUTH_URL);
				LOGGER.info("Caching new auth token for username: {}", tokenKey);
				cacheService.putAuth(dto);
				responseDTO = dto;
			}
		} catch (ServiceException e) {
			LOGGER.error("Service exception during token validation: {}", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Unexpected error during token validation: {}", e.getMessage(), e);
			throw e;
		}
		return responseDTO;
	}

	@Override
	public AuthResponseDTO validateAuthCode(String authCode) {
		LOGGER.info("Validating authCode in cache: {}", authCode);
		AuthResponseDTO response = null;
		try {
			AuthResponseDTO byKey = cacheService.getAuthByToken(authCode);
			if (byKey != null && authCode.equals(byKey.getAccessToken())) {
				response = byKey;
			} else {
				LOGGER.warn("authCode not found in cache: {}", authCode);
				throw new ServiceException(ErrorCode.INVALID_TOKEN);
			}
		} catch (ServiceException e) {
			LOGGER.error("Service exception during token validation: {}", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Unexpected error during token validation: {}", e.getMessage(), e);
			throw e;
		}
		return response;

	}
}
