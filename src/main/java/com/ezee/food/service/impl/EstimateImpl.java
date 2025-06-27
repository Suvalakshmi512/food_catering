package com.ezee.food.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ezee.food.Exception.ErrorCode;
import com.ezee.food.Exception.ServiceException;
import com.ezee.food.cache.redis.service.RedisDishService;
import com.ezee.food.cache.redis.service.RedisEventService;
import com.ezee.food.cache.redis.service.RedisMenuService;
import com.ezee.food.dao.EstimateDAO;
import com.ezee.food.dao.UserCustomerDAO;
import com.ezee.food.dto.AuthResponseDTO;
import com.ezee.food.dto.DishDTO;
import com.ezee.food.dto.DishListDTO;
import com.ezee.food.dto.EstimateDTO;
import com.ezee.food.dto.EventDTO;
import com.ezee.food.dto.MenuDTO;
import com.ezee.food.dto.UserCustomerDTO;
import com.ezee.food.service.AuthService;
import com.ezee.food.service.DishService;
import com.ezee.food.service.EstimateService;
import com.ezee.food.util.CodeGenarator;

@Service
public class EstimateImpl implements EstimateService {
	@Autowired
	private AuthService authService;
	@Autowired
	private EstimateDAO estimateDAO;
	@Autowired
	private RedisEventService eventCache;
	@Autowired
	private RedisMenuService menuCache;
	@Autowired
	private RedisDishService dishCache;
	@Autowired
	private DishService dish;
	@Autowired
	private UserCustomerDAO customer;

	@Override
	public EstimateDTO getEstimateByCode(String code, String authCode) {
		authService.validateAuthCode(authCode);
		EstimateDTO estimate = new EstimateDTO();
		estimate.setCode(code);
		EstimateDTO estimateDTO = estimateDAO.getEstimate(estimate);
		if (estimateDTO.getMenuDTO() == null || estimateDTO.getMenuDTO().getId() == 0) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		EventDTO event = eventCache.getEventFromCache(estimateDTO.getEventDTO());
		estimateDTO.setEventDTO(event);
		MenuDTO menu = menuCache.getMenuFromCache(estimateDTO.getMenuDTO());
		if (menu.getDishListDTO() == null) {
			throw new ServiceException("DishList is null");
		}
		for (DishListDTO dishList : menu.getDishListDTO()) {
			DishDTO dishDTO = dishCache.getDishFromCache(dishList.getDishDTO());
			DishDTO enrichedDish = dish.enrichDishDetails(dishDTO);

			dishList.setDishDTO(enrichedDish);
		}
		estimateDTO.setMenuDTO(menu);
		return estimateDTO;
	}

	@Override
	public void addEstimate(EstimateDTO estimateDTO, String authCode) {
		AuthResponseDTO validateAuthCode = authService.validateAuthCode(authCode);
		estimateDTO.setUpdatedBy(validateAuthCode.getUsername());
		estimateDTO.setCode(CodeGenarator.generateCode("EST", 12));
		estimateDAO.addEstimate(estimateDTO);
	}

	@Override
	public List<EstimateDTO> getEstimate(String authCode) {
		authService.validateAuthCode(authCode);
		List<EstimateDTO> list = estimateDAO.getAllEstimate();
		for (EstimateDTO estimateDTO : list) {
			if (estimateDTO.getMenuDTO() == null || estimateDTO.getMenuDTO().getId() == 0) {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}
			EventDTO event = eventCache.getEventFromCache(estimateDTO.getEventDTO());
			UserCustomerDTO userCustomer = customer.getCustomer(estimateDTO.getEventDTO().getCustomerDTO());
			event.setCustomerDTO(userCustomer);
			estimateDTO.setEventDTO(event);
			MenuDTO menu = menuCache.getMenuFromCache(estimateDTO.getMenuDTO());
			if (menu.getDishListDTO() == null) {
				throw new ServiceException("DishList is null");
			}
			for (DishListDTO dishList : menu.getDishListDTO()) {
				DishDTO dishDTO = dishCache.getDishFromCache(dishList.getDishDTO());
				DishDTO enrichedDish = dish.enrichDishDetails(dishDTO);

				dishList.setDishDTO(enrichedDish);
			}
			estimateDTO.setMenuDTO(menu);
		}
		return list;
	}
}
