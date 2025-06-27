package com.ezee.food.service.impl;

import java.util.List;
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

	@Override
	public List<DishListDTO> getAllDishList(String authCode) {
		authService.validateAuthCode(authCode);
		List<DishListDTO> list = dao.getAllDishList();
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
		return list;
	}

	@Override
	public DishListDTO getDishListByCode(DishListDTO dishListDTO, String authCode) {
		authService.validateAuthCode(authCode);
		DishListDTO data = dao.getDishList(dishListDTO);
		dishCache.getDishFromCache(data.getDishDTO());
		if (data.getDishDTO().getId() == 0) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		menuDAO.getMenu(data.getMenuDTO());
		if (data.getMenuDTO().getId() == 0) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		return dao.getDishList(dishListDTO);
	}
}
