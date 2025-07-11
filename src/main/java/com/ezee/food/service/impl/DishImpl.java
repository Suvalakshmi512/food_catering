package com.ezee.food.service.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezee.food.Exception.ErrorCode;
import com.ezee.food.Exception.ServiceException;
import com.ezee.food.cache.redis.service.RedisIngredientService;
import com.ezee.food.cache.redis.service.RedisLabourService;
import com.ezee.food.cache.redis.service.RedisTaxService;
import com.ezee.food.dao.DishDAO;
import com.ezee.food.dao.DishIngredientDAO;
import com.ezee.food.dao.DishLabourDAO;
import com.ezee.food.dto.AuthResponseDTO;
import com.ezee.food.dto.DishDTO;
import com.ezee.food.dto.DishIngredientDTO;
import com.ezee.food.dto.DishLabourDTO;
import com.ezee.food.dto.IngredientDTO;
import com.ezee.food.dto.LabourDTO;
import com.ezee.food.dto.TaxDTO;
import com.ezee.food.service.AuthService;
import com.ezee.food.service.DishService;
import com.ezee.food.util.CodeGenarator;

import lombok.Cleanup;

@Service
public class DishImpl implements DishService {
	@Autowired
	private DataSource dataSource;
	@Autowired
	private DishDAO dishDAO;
	@Autowired
	private DishIngredientDAO dishIngredientDAO;
	@Autowired
	private AuthService authService;
	@Autowired
	private RedisLabourService labour;
	@Autowired
	private RedisTaxService taxCache;
	@Autowired
	private DishLabourDAO dishLabourDAO;
	@Autowired
	private RedisIngredientService ingredient;

	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	@Override
	public List<DishDTO> getAllDish(String authCode) {
		List<DishDTO> allDish = new ArrayList<DishDTO>();
		try {
			AuthResponseDTO authDTO = authService.validateAuthCode(authCode);
			if (authDTO != null) {
				allDish = dishDAO.getAllDish();
				for (DishDTO dish : allDish) {
					enrichDishDetails(dish);
				}
			}

		} catch (ServiceException se) {
			LOGGER.error("Service exception while getting all Dish: {}", se.getMessage(), se);
			throw se;
		} catch (Exception e) {
			LOGGER.error("Unexpected error while getting all Dish: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching Dish");
		}
		return allDish;
	}

	@Override
	public DishDTO getDishByCode(String code, String authCode) {
		DishDTO dish = new DishDTO();
		try {
			authService.validateAuthCode(authCode);

			DishDTO dishDTO = new DishDTO();
			dishDTO.setCode(code);

			dish = dishDAO.getDish(dishDTO);
		} catch (ServiceException se) {
			LOGGER.error("Service exception while getting Dish: {}", se.getMessage(), se);
			throw se;
		} catch (Exception e) {
			LOGGER.error("Error while getting Dish: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching Dish");

		}
		return enrichDishDetails(dish);
	}

	@Override
	public void addDish(DishDTO dishDTO, String authCode) {
		try {
		@Cleanup
		Connection connection = dataSource.getConnection();
		try {
			connection.setAutoCommit(false);
			AuthResponseDTO validateAuthCode = authService.validateAuthCode(authCode);
			String username = validateAuthCode.getUsername();
			dishDTO.setUpdatedby(username);
			dishDTO.setCode(CodeGenarator.generateCode("DIH", 12));
			dishDAO.insertDish(dishDTO, connection);

			if (dishDTO.getId() == 0) {
				throw new ServiceException("Dish insert failed.");
			}

			for (DishIngredientDTO ingredient : dishDTO.getDishIngredientList()) {
				ingredient.setUpdatedby(validateAuthCode.getUsername());
				ingredient.setCode(CodeGenarator.generateCode("DHIG", 11));
				ingredient.setDishDTO(dishDTO);
				int dishIngredient = dishIngredientDAO.addDishIngredient(ingredient, connection);

				if (dishIngredient == 0) {
					throw new ServiceException("Ingredient insert failed.");
				}
			}
			for (DishLabourDTO dishlabour : dishDTO.getDishLabourList()) {
				dishlabour.setUpdatedby(validateAuthCode.getUsername());
				dishlabour.setCode(CodeGenarator.generateCode("DSLB", 11));
				dishlabour.setDishDTO(dishDTO);
				int dishlab = dishLabourDAO.addDishLabour(dishlabour, connection);

				if (dishlab == 0) {
					throw new ServiceException("dishLabour insert failed.");
				}
			}
         	

		} catch (ServiceException se) {
			LOGGER.error("Service exception while adding dish: {}", se.getMessage(), se);
			throw se;
		} 
		finally {
			connection.commit();
			connection.setAutoCommit(true);
		}
		}catch (Exception e) {
			LOGGER.error("Unexpected error while adding dish: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while adding dish");
		}
	}

	@Override
	public DishDTO enrichDishDetails(DishDTO dish) {
		try {
			if (dish.getTaxDTO().getId() == 0) {
				throw new ServiceException("Tax ID is 0 for dish: " + dish.getCode());
			}

			TaxDTO fullTaxDTO = taxCache.getTaxFromCache(dish.getTaxDTO());
			dish.setTaxDTO(fullTaxDTO);

			for (DishIngredientDTO ing : dish.getDishIngredientList()) {
				IngredientDTO fullIng = ingredient.getIngredientFromCache(ing.getIngredientDTO());
				ing.setIngredientDTO(fullIng);
			}

			for (DishLabourDTO lab : dish.getDishLabourList()) {
				LabourDTO fullLabour = labour.getLabourFromCache(lab.getLabourDTO());
				lab.setLabourDTO(fullLabour);
			}
		} catch (Exception e) {
			throw new ServiceException("Error adding dish: " + e.getMessage(), e);
		}

		return dish;
	}
	@Override
	public void updatePrice(int id, String authcode) {
		try {
		int totalUpdated = dishDAO.calculateAndUpdateDishPrice(id);
		if (totalUpdated == 0) {
			throw new ServiceException("Failed to update Dish total price.");
		}
		} catch (ServiceException se) {
			LOGGER.error("Service exception while updating dish: {}", se.getMessage(), se);
			throw se;
		} catch (Exception e) {
			throw new ServiceException("Error updating dish: " + e.getMessage(), e);
		}
		
	}
}
