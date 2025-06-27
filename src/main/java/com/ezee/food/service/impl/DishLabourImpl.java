package com.ezee.food.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezee.food.Exception.ErrorCode;
import com.ezee.food.Exception.ServiceException;
import com.ezee.food.cache.redis.service.RedisDishService;
import com.ezee.food.dao.DishLabourDAO;
import com.ezee.food.dao.LabourDAO;
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
	private LabourDAO labourDAO;

	@Override
	public List<DishLabourDTO> getAllDishLabour(String authCode) {
		authService.validateAuthCode(authCode);
		List<DishLabourDTO> list = dao.getAllDishLabour();
		for (DishLabourDTO data : list) {
			dishCache.getDishFromCache(data.getDishDTO());
			if (data.getDishDTO().getId() == 0) {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}

			labourDAO.getLabour(data.getLabourDTO());
			if (data.getLabourDTO().getId() == 0) {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}
		}
		return list;
	}

	@Override
	public DishLabourDTO getDishLabourByCode(DishLabourDTO dishLabourDTO, String authCode) {
		authService.validateAuthCode(authCode);
		DishLabourDTO dishLabour = dao.getDishLabour(dishLabourDTO);
		labourDAO.getLabour(dishLabour.getLabourDTO());
		if (dishLabourDTO.getLabourDTO().getId() == 0) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		dishCache.getDishFromCache(dishLabour.getDishDTO());
		if (dishLabourDTO.getDishDTO().getId() == 0) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		return dishLabour;
	}
}
