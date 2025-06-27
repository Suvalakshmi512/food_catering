package com.ezee.food.controller.io;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DishLabourIO extends BaseIO{
	private LabourIO labourIO;
	private DishIO dishIO;
	private BigDecimal hoursRequired;
	private String updatedby;
	private String updatedAt;

}
