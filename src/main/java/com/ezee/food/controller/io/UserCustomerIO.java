package com.ezee.food.controller.io;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserCustomerIO extends BaseIO {
	private String email;
	private String mobile;
}
