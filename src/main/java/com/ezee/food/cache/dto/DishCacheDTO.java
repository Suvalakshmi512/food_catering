package com.ezee.food.cache.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.ezee.food.dto.DishIngredientDTO;
import com.ezee.food.dto.DishLabourDTO;
import com.ezee.food.dto.TaxDTO;

import lombok.Data;
@Data
public class DishCacheDTO implements Serializable {
	private static final long serialVersionUID = 7643540122205147224L;
	private int id;
	private String code;
	private String name;
	private int activeFlag;
	private String description;
	private int timeToMake;
	private String vegType;
	private int minAvailableQuantity;
	private int servingSize;
	private BigDecimal marginProfit;
	private TaxDTO taxDTO;
	private BigDecimal price;
	private List<DishIngredientDTO> dishIngredientList;
	private List<DishLabourDTO> dishLabourList;

}
