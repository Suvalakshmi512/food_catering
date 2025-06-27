package com.ezee.food.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EstimateDTO extends BaseDTO{
	private EventDTO eventDTO;
	private MenuDTO menuDTO;
	private BigDecimal subTotal;
	private double discount;
	private BigDecimal grantTotal;
	private String updatedBy;
	private String updatedAt;

}
