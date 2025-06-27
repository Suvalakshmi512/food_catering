package com.ezee.food.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserCustomerDTO extends BaseDTO {
	private String email;
	private String mobile;
	private String updatedBy;
	private String updatedAt;
}
