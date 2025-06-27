package com.ezee.food.cache.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserCustomerCacheDTO implements Serializable {
	private static final long serialVersionUID = 7643540122205147224L;
	private int id;
	private String code;
	private String name;
	private int activeFlag;
	private String email;
	private String mobile;

}
