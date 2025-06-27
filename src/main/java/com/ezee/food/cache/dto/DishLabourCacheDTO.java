package com.ezee.food.cache.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.ezee.food.dto.DishDTO;
import com.ezee.food.dto.LabourDTO;

import lombok.Data;
@Data
public class DishLabourCacheDTO implements Serializable{
	private static final long serialVersionUID = -4365121145815670126L;
	private int id;
	private String code;
	private int activeFlag;
	private LabourDTO labourDTO;
	private DishDTO dishDTO;
	private BigDecimal hoursRequired;
}
