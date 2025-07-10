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
import com.ezee.food.cache.redis.service.RedisLabourService;
import com.ezee.food.dao.DishLabourDAO;
import com.ezee.food.dto.DishLabourDTO;
import com.ezee.food.service.AuthService;
import com.ezee.food.service.DishLabourService;

@Service
public class DishLabourImpl implements DishLabourService {
	@Autowired
	private AuthService authService;
	@Autowired
	private DishLabourDAO dao;
	@Autowired
	private RedisDishService dishCache;
	@Autowired
	private RedisLabourService labour;

	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	@Override
	public List<DishLabourDTO> getAllDishLabour(String authCode) {
		List<DishLabourDTO> list = new ArrayList<DishLabourDTO>();
		try {
			authService.validateAuthCode(authCode);
			list = dao.getAllDishLabour();
			for (DishLabourDTO data : list) {
				dishCache.getDishFromCache(data.getDishDTO());
				if (data.getDishDTO().getId() == 0) {
					throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
				}

				labour.getLabourFromCache(data.getLabourDTO());
				if (data.getLabourDTO().getId() == 0) {
					throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
				}
			}
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error while getting all DishLabour: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching DishLabour");
		}
		return list;
	}

	@Override
	public DishLabourDTO getDishLabourByCode(DishLabourDTO dishLabourDTO, String authCode) {
		DishLabourDTO dishLabour = new DishLabourDTO();
		try {
			authService.validateAuthCode(authCode);
			dishLabour = dao.getDishLabour(dishLabourDTO);
			labour.getLabourFromCache(dishLabour.getLabourDTO());
			if (dishLabourDTO.getLabourDTO().getId() == 0) {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}
			dishCache.getDishFromCache(dishLabour.getDishDTO());
			if (dishLabourDTO.getDishDTO().getId() == 0) {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error while getting DishLabour: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching DishLabour");
		}
		return dishLabour;
	}
}
