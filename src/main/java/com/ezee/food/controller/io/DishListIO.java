package com.ezee.food.controller.io;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
public class DishListIO extends BaseIO{
	private MenuIO menu;
	private DishIO dish;
	private BigDecimal unitPrice;
}
