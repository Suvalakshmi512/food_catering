package com.ezee.food.controller.io;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuIO extends BaseIO {
	private List<DishListIO> dishList;
	private BigDecimal price;
}
