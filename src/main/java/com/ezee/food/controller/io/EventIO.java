package com.ezee.food.controller.io;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventIO extends BaseIO{
	private UserCustomerIO customer;
	private String eventDate;
	private String eventTime;
	private String venue;
	private int guestCount;
	
}
