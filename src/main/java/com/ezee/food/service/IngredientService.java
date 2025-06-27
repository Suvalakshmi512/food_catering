package com.ezee.food.service;

import java.util.List;
import java.util.Map;

import com.ezee.food.dto.IngredientDTO;


public interface IngredientService {
	public List<IngredientDTO> getAllIngredient(String authCode);

	public IngredientDTO getIngredientByCode(String code, String authCode);

	public void addIngredient(IngredientDTO ingredientDTO, String authCode);

	public void update(Map<String, Object> ingredient, IngredientDTO ingredientDTO, String authCode);

}
