package com.ezee.food.service;

import java.util.List;
import com.ezee.food.dto.DishDTO;

public interface DishService {
	public List<DishDTO> getAllDish(String authCode);

	public DishDTO getDishByCode(String code, String authCode);

	public void addDish(DishDTO dishListDTO, String authCode);
	
	public DishDTO enrichDishDetails(DishDTO dish);

	public void updatePrice(int id, String authcode);

}
