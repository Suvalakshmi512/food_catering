package com.ezee.food.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ezee.food.controller.io.ResponseIO;
import com.ezee.food.dto.AuthDTO;
import com.ezee.food.dto.AuthResponseDTO;
import com.ezee.food.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/token")
	public ResponseIO<AuthResponseDTO> generateToken(@RequestBody AuthDTO authDTO) {
		AuthResponseDTO authToken = authService.authToken(authDTO);
		return ResponseIO.success(authToken);
	}
}
