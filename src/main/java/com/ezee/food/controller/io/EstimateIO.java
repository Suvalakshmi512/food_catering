package com.ezee.food.controller.io;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstimateIO extends BaseIO{
	private EventIO eventIO;
	private MenuIO menuIO;
	private BigDecimal subTotal;
	private double discount;
	private BigDecimal grantTotal;

}
