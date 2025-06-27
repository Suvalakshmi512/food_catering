package com.ezee.food.service;

import java.util.List;
import com.ezee.food.dto.DishListDTO;

public interface DishListService {
	public List<DishListDTO> getAllDishList(String authCode);

	public DishListDTO getDishListByCode(DishListDTO dishListDTO, String authCode);


}