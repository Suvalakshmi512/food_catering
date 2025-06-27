package com.ezee.food.cache.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.ezee.food.dto.TaxDTO;

import lombok.Data;

@Data
public class IngredientCacheDTO implements Serializable{
	private static final long serialVersionUID = -4365121145815670126L;
	private int id;
	private int activeFlag;
	private String code;
	private String name;
	private BigDecimal unitQuantity;
	private String unit;
	private BigDecimal unitCost;
	private TaxDTO taxDTO;

}
