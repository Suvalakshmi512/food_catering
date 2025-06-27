package com.ezee.food.controller.io;

import java.math.BigDecimal;

import com.ezee.food.dto.DishDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DishIngredientIO extends BaseIO{
	private DishIO dishIO;
	private IngredientIO ingredientIO;
	private BigDecimal qunatityUsed;
	private BigDecimal wastage;
	private BigDecimal price;
}
