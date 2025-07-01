package com.ezee.food.controller.io;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DishLabourIO extends BaseIO{
	private LabourIO labour;
	private DishIO dish;
	private BigDecimal hoursRequired;
	private String updatedby;
	private String updatedAt;

}
