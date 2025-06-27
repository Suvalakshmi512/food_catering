package com.ezee.food.cache.dto;

import java.io.Serializable;

import lombok.Data;
@Data
public class AuthCacheDTO implements Serializable {
	private static final long serialVersionUID = 7643540122205147224L;
	private String username;
	private String accessToken;
	

}
