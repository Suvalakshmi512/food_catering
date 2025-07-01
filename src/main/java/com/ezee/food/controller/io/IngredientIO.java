package com.ezee.food.controller.io;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IngredientIO extends BaseIO {
	private BigDecimal unitQuantity;
	private String unit;
	private BigDecimal unitCost;
	private TaxIO tax;
	

}
