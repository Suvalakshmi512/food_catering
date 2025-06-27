package com.ezee.food.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventDTO extends BaseDTO{
	private UserCustomerDTO customerDTO;
	private String eventDate;
	private String eventTime;
	private String venue;
	private int guestCount;
	private String updatedby;
	private String updatedAt;

}
