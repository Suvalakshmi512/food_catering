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
import com.ezee.food.cache.redis.service.RedisDishService;
import com.ezee.food.dao.DishListDAO;
import com.ezee.food.dao.MenuDAO;
import com.ezee.food.dto.AuthResponseDTO;
import com.ezee.food.dto.DishDTO;
import com.ezee.food.dto.DishListDTO;
import com.ezee.food.dto.MenuDTO;
import com.ezee.food.service.AuthService;
import com.ezee.food.service.DishService;
import com.ezee.food.service.MenuService;
import com.ezee.food.util.CodeGenarator;

import lombok.Cleanup;

@Service
public class MenuImpl implements MenuService {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private MenuDAO menuDAO;
	@Autowired
	private DishListDAO dishListDAO;
	@Autowired
	private AuthService authService;
	@Autowired
	private DishService dish;
	@Autowired
	private RedisDishService dishCache;

	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	@Override
	public List<MenuDTO> getAllMenu(String authCode) {
		List<MenuDTO> allMenu =new ArrayList<MenuDTO>();
		try {
		authService.validateAuthCode(authCode);
		allMenu = menuDAO.getAllMenu();

		for (MenuDTO menu : allMenu) {
			for (DishListDTO dishList : menu.getDishListDTO()) {
				DishDTO dishDTO = dishCache.getDishFromCache(dishList.getDishDTO());
				DishDTO enrichedDish = dish.enrichDishDetails(dishDTO);

				dishList.setDishDTO(enrichedDish);
			}
		}
		}catch (Exception e) {
			LOGGER.error("Error while getting all menus: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching menus");
		}

		return allMenu;
	}

	@Override
	public MenuDTO getMenuByCode(String code, String authCode) {
		MenuDTO menu = new MenuDTO();
		try {
		authService.validateAuthCode(authCode);
		MenuDTO menuDTO = new MenuDTO();
		menuDTO.setCode(code);
		menu = menuDAO.getMenu(menuDTO);
		if (menu.getDishListDTO() == null) {
			throw new ServiceException("DishList is null");
		}
		for (DishListDTO dishList : menu.getDishListDTO()) {
			DishDTO dishDTO = dishCache.getDishFromCache(dishList.getDishDTO());
			DishDTO enrichedDish = dish.enrichDishDetails(dishDTO);

			dishList.setDishDTO(enrichedDish);
		}
		}catch (Exception e) {
			LOGGER.error("Error while getting menu: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching menu");
		}
		return menu;
	}

	@Override
	public void addMenu(MenuDTO menuDTO, String authCode) {
		try {
			@Cleanup
			Connection connection = dataSource.getConnection();
			connection.setAutoCommit(false);

			AuthResponseDTO validateAuthCode = authService.validateAuthCode(authCode);
			menuDTO.setUpdatedby(validateAuthCode.getUsername());
			menuDTO.setCode(CodeGenarator.generateCode("MNU", 12));
			menuDAO.insertMenu(menuDTO, connection);
			if (menuDTO.getId() == 0) {
				throw new ServiceException("Menu insert failed.");
			}

			for (DishListDTO dishListDTO : menuDTO.getDishListDTO()) {
				dishListDTO.setMenuDTO(menuDTO);
				dishListDTO.setUpdatedby(validateAuthCode.getUsername());
				dishListDTO.setCode(CodeGenarator.generateCode("DHLT", 11));
				int dishList = dishListDAO.addDishList(dishListDTO, connection);
				if (dishList == 0) {
					throw new ServiceException("DishList insert failed.");
				}
			}

			LOGGER.info("Before commit");
			connection.commit();
			LOGGER.info("After commit");

			connection.setAutoCommit(true);

			int totalUpdated = menuDAO.calculateAndUpdateMenuPrice(menuDTO.getId(), connection);
			if (totalUpdated == 0) {
				throw new ServiceException("Failed to update menu total price.");
			}

		} catch (Exception e) {
			throw new ServiceException("Error adding menu: " + e.getMessage(), e);
		}
	}

}