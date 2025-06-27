package com.ezee.food.service;

import java.util.List;
import com.ezee.food.dto.DishIngredientDTO;


public interface DishIngredientService {
	public List<DishIngredientDTO> getAllDishIngredient(String authCode);

	public DishIngredientDTO getDishIngredientByCode(DishIngredientDTO dishIngredientDTO, String authCode);

}
