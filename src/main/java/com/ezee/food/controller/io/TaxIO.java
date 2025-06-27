package com.ezee.food.controller.io;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
public class TaxIO extends BaseIO{
	private String description;
	private BigDecimal ratePercentage;

}
