package com.ezee.food.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LabourDTO extends BaseDTO {
	private String roleName;
	private BigDecimal hourslySalary;
	private String specialization;
	private String updatedby;
	private String updatedAt;
}
