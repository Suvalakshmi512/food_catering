package com.ezee.food.service;

import java.util.List;
import com.ezee.food.dto.DishLabourDTO;

public interface DishLabourService {
	public List<DishLabourDTO> getAllDishLabour(String authCode);

	public DishLabourDTO getDishLabourByCode(DishLabourDTO dishLabourDTO, String authCode);
}