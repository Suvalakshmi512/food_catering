package com.ezee.food.service;

import com.ezee.food.dto.AuthDTO;
import com.ezee.food.dto.AuthResponseDTO;

public interface AuthService {
	
	public AuthResponseDTO authToken(AuthDTO authDTO);
	public AuthResponseDTO validateAuthCode(String authCode);

}
