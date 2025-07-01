package com.ezee.food.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezee.food.Exception.ErrorCode;
import com.ezee.food.Exception.ServiceException;
import com.ezee.food.cache.redis.service.RedisDishService;
import com.ezee.food.cache.redis.service.RedisIngredientService;
import com.ezee.food.dao.DishIngredientDAO;
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

	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	@Override
	public List<DishIngredientDTO> getAllDishIngredient(String authCode) {
		List<DishIngredientDTO> list = new ArrayList<DishIngredientDTO>();
		try {
			authService.validateAuthCode(authCode);
			list = dao.getAllDishIngredient();
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
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error while getting all DishIngredient: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching DishIngredient");

		}
		return list;
	}

	@Override
	public DishIngredientDTO getDishIngredientByCode(DishIngredientDTO dishIngredientDTO, String authCode) {
		DishIngredientDTO ingredient = new DishIngredientDTO();
		try {
			authService.validateAuthCode(authCode);
			ingredient = dao.getIngredient(dishIngredientDTO);
			dishCache.getDishFromCache(ingredient.getDishDTO());
			if (dishIngredientDTO.getDishDTO().getId() == 0) {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}

			ingredientCache.getIngredientFromCache(ingredient.getIngredientDTO());
			if (dishIngredientDTO.getIngredientDTO().getId() == 0) {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error while getting DishIngredient: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching DishIngredient");
		}
		return ingredient;
	}
}
