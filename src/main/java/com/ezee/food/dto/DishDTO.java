package com.ezee.food.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DishDTO extends BaseDTO {
	private String description;
	private int timeToMake;
	private String vegType;
	private int minAvailableQuantity;
	private int servingSize;
	private BigDecimal marginProfit;
	private TaxDTO taxDTO;
	private BigDecimal price;
	private List<DishIngredientDTO> dishIngredientList;
	private List<DishLabourDTO> dishLabourList;
	private String updatedby;
	private String updatedAt;
	

}
