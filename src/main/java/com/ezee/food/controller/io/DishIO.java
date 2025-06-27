package com.ezee.food.controller.io;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
public class DishIO extends BaseIO{
	private String description;
	private int timeToMake;
	private String vegType;
	private int minAvailableQuantity;
	private int servingSize;
	private BigDecimal marginProfit;
	private TaxIO taxIO;
	private BigDecimal price;
	private List<DishIngredientIO> dishIngredientIO;
	private List<DishLabourIO> dishLabourIO;

}
