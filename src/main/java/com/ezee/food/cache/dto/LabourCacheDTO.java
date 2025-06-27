package com.ezee.food.cache.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
@Data
public class LabourCacheDTO implements Serializable {
	private static final long serialVersionUID = 7643540122205147224L;
	private int id;
	private int activeFlag;
	private String code;
	private String name;
	private String roleName;
	private BigDecimal hourslySalary;
	private String specialization;

}
