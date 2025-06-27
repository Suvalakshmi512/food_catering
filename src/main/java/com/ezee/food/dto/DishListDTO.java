package com.ezee.food.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DishListDTO extends BaseDTO {
	private MenuDTO menuDTO;
	private DishDTO dishDTO;
	private BigDecimal unitPrice;
	private String updatedby;
	private String updatedAt;

}
