package com.ezee.food.cache.redis.service;

import com.ezee.food.dto.AuthResponseDTO;

public interface RedisAuthService {
	public AuthResponseDTO getAuthByUsername(String username);
	public AuthResponseDTO getAuthByToken(String token);
	public void putAuth(AuthResponseDTO dto);
	public void clearAuth(String key);
}
