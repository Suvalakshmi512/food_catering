package com.ezee.food.controller.io;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LabourIO extends BaseIO {
	private String roleName;
	private BigDecimal hourslySalary;
	private String specialization;
}
