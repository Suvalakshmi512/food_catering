package com.ezee.food.cache.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.ezee.food.dto.DishListDTO;

import lombok.Data;
@Data
public class MenuCacheDTO implements Serializable {
	private static final long serialVersionUID = 7643540122205147224L;
	private int id;
	private String code;
	private String name;
	private int activeFlag;
	private List<DishListDTO> dishListDTO;
	private BigDecimal price;

}
