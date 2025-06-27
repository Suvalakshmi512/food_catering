package com.ezee.food.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IngredientDTO extends BaseDTO {
	private BigDecimal unitQuantity;
	private String unit;
	private BigDecimal unitCost;
	private TaxDTO taxDTO;
	private String updatedby;
	private String updatedAt;
	
	

}
