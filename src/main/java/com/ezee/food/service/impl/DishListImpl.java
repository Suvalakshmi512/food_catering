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
import com.ezee.food.dao.DishListDAO;
import com.ezee.food.dao.MenuDAO;
import com.ezee.food.dto.DishListDTO;
import com.ezee.food.service.AuthService;
import com.ezee.food.service.DishListService;

@Service
public class DishListImpl implements DishListService {
	@Autowired
	private AuthService authService;
	@Autowired
	private DishListDAO dao;
	@Autowired
	private RedisDishService dishCache;
	@Autowired
	private MenuDAO menuDAO;

	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	@Override
	public List<DishListDTO> getAllDishList(String authCode) {
		List<DishListDTO> list = new ArrayList<DishListDTO>();
		try {
			authService.validateAuthCode(authCode);
			list = dao.getAllDishList();
			for (DishListDTO data : list) {
				dishCache.getDishFromCache(data.getDishDTO());
				if (data.getDishDTO().getId() == 0) {
					throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
				}
				menuDAO.getMenu(data.getMenuDTO());
				if (data.getMenuDTO().getId() == 0) {
					throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
				}

			}
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error while getting all DishList: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching DishList");
		}
		return list;
	}

	@Override
	public DishListDTO getDishListByCode(DishListDTO dishListDTO, String authCode) {
		DishListDTO data = new DishListDTO();
		try {
			authService.validateAuthCode(authCode);
			data = dao.getDishList(dishListDTO);
			dishCache.getDishFromCache(data.getDishDTO());
			if (data.getDishDTO().getId() == 0) {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}
			menuDAO.getMenu(data.getMenuDTO());
			if (data.getMenuDTO().getId() == 0) {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}
			return dao.getDishList(dishListDTO);
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error while getting  DishList: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching DishList");
		}
	}
}
