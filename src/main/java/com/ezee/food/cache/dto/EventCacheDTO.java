package com.ezee.food.cache.dto;

import java.io.Serializable;

import com.ezee.food.dto.UserCustomerDTO;

import lombok.Data;

@Data
public class EventCacheDTO implements Serializable {
	private static final long serialVersionUID = 7643540122205147224L;
	private int id;
	private String name;
	private String code;
	private int activeFlag;
	private UserCustomerDTO customerDTO;
	private String eventDate;
	private String eventTime;
	private String venue;
	private int guestCount;

}
