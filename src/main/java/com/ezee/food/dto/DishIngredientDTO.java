package com.ezee.food.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DishIngredientDTO extends BaseDTO {
	private DishDTO dishDTO;
	private IngredientDTO ingredientDTO;
	private BigDecimal qunatityUsed;
	private BigDecimal wastage;
	private BigDecimal price;
	private String updatedby;
	private String updatedAt;
}
