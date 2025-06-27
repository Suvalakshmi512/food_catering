package com.ezee.food.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezee.food.Exception.ErrorCode;
import com.ezee.food.Exception.ServiceException;
import com.ezee.food.cache.redis.service.RedisDishService;
import com.ezee.food.cache.redis.service.RedisIngredientService;
import com.ezee.food.dao.DishIngredientDAO;
import com.ezee.food.dao.IngredientDAO;
import com.ezee.food.dto.DishIngredientDTO;
import com.ezee.food.service.AuthService;
import com.ezee.food.service.DishIngredientService;

@Service
public class DishIngredientImpl implements DishIngredientService {
	@Autowired
	private AuthService authService;

	@Autowired
	private DishIngredientDAO dao;

	@Autowired
	private RedisDishService dishCache;

	@Autowired
	private RedisIngredientService ingredientCache;


	@Override
	public List<DishIngredientDTO> getAllDishIngredient(String authCode) {
		authService.validateAuthCode(authCode);
		List<DishIngredientDTO> list = dao.getAllDishIngredient();
		for (DishIngredientDTO data : list) {
			dishCache.getDishFromCache(data.getDishDTO());
			if (data.getDishDTO().getId() == 0) {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}

			ingredientCache.getIngredientFromCache(data.getIngredientDTO());
			if (data.getIngredientDTO().getId() == 0) {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}
		}
		return list;
	}

	@Override
	public DishIngredientDTO getDishIngredientByCode(DishIngredientDTO dishIngredientDTO, String authCode) {
		authService.validateAuthCode(authCode);
		DishIngredientDTO ingredient = dao.getIngredient(dishIngredientDTO);
		dishCache.getDishFromCache(ingredient.getDishDTO());
		if (dishIngredientDTO.getDishDTO().getId() == 0) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}

		ingredientCache.getIngredientFromCache(ingredient.getIngredientDTO());
		if (dishIngredientDTO.getIngredientDTO().getId() == 0) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		return ingredient;
	}
}
