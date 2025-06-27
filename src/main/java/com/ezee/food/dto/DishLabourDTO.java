package com.ezee.food.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DishLabourDTO extends BaseDTO{
	private LabourDTO labourDTO;
	private DishDTO dishDTO;
	private BigDecimal hoursRequired;
	private String updatedby;
	private String updatedAt;
}
 