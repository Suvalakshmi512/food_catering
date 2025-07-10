package com.ezee.food.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezee.food.Exception.ErrorCode;
import com.ezee.food.Exception.ServiceException;
import com.ezee.food.cache.redis.service.RedisUserCustomerService;
import com.ezee.food.dao.EventDAO;
import com.ezee.food.dto.AuthResponseDTO;
import com.ezee.food.dto.EventDTO;
import com.ezee.food.dto.UserCustomerDTO;
import com.ezee.food.service.AuthService;
import com.ezee.food.service.EventService;
import com.ezee.food.util.CodeGenarator;
import com.ezee.food.util.DTOUtils;

@Service
public class EventImpl implements EventService {
	@Autowired
	private AuthService authService;
	@Autowired
	private EventDAO dao;
	@Autowired
	private RedisUserCustomerService userCustomer;

	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	@Override
	public List<EventDTO> getAllEvent(String authCode) {
		List<EventDTO> list = null;
		try {
			authService.validateAuthCode(authCode);
			list = dao.getAllEvent();
			for (EventDTO data : list) {
				UserCustomerDTO customer = userCustomer.getUserCustomerFromCache(data.getCustomerDTO());
				data.setCustomerDTO(customer);
				if (data.getCustomerDTO().getId() == 0) {
					throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
				}
			}

		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error while getting all events: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching events");
		}
		return list;
	}

	@Override
	public EventDTO getEventByCode(String code, String authCode) {
		EventDTO eventDTO = new EventDTO();
		try {
			authService.validateAuthCode(authCode);
			eventDTO.setCode(code);
			eventDTO = dao.getEvent(eventDTO);
			if (eventDTO.getCustomerDTO() == null || eventDTO.getCustomerDTO().getId() == 0) {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}
			UserCustomerDTO customer = userCustomer.getUserCustomerFromCache(eventDTO.getCustomerDTO());
			eventDTO.setCustomerDTO(customer);

		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error while getting all events: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching events");
		}
		return eventDTO;
	}

	@Override
	public void addEvent(EventDTO eventDTO, String authCode) {
		try {
			AuthResponseDTO validateAuthCode = authService.validateAuthCode(authCode);
			eventDTO.setUpdatedby(validateAuthCode.getUsername());
			eventDTO.setCode(CodeGenarator.generateCode("EVT", 12));
			dao.addEvent(eventDTO);
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error while adding events: {}", e.getMessage(), e);
			e.printStackTrace();
		}
	}

	@Override
	public void update(Map<String, Object> event, EventDTO eventDTO, String authCode) {
		AuthResponseDTO validateAuthCode = authService.validateAuthCode(authCode);
		String code = eventDTO.getCode();
		if (code == null) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		EventDTO dto = dao.getEvent(eventDTO);
		if (dto.getId() == 0) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		try {
			if (event != null) {
				event.forEach((key, value) -> {
					switch (key) {
					case "venue":
						dto.setVenue((String) value);
						break;
					case "eventTime":
						dto.setEventTime((String) value);
						break;
					case "eventDate":
						dto.setEventDate((String) value);
						break;
					case "guestCount":
						dto.setGuestCount((int) value);
						break;
					case "activeFlag":
						dto.setActiveFlag((int) value);
						break;
					case "customerDTO":
						String customerCode = DTOUtils.extractForeignKeyCode(value);
						if (customerCode != null) {
							UserCustomerDTO customerSearchDTO = new UserCustomerDTO();
							customerSearchDTO.setCode(customerCode);
							dto.setCustomerDTO(customerSearchDTO);
						}
						break;

					default:
						throw new ServiceException(ErrorCode.KEY_NOT_FOUND_EXCEPTION);
					}
				});
				dto.setUpdatedby(validateAuthCode.getUsername());
				int updatedRows = dao.addEvent(dto);
				if (updatedRows == 0) {
					throw new ServiceException(ErrorCode.UPDATE_FAILED_EXCEPTION);
				}
			} else {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Unexpected error: {}", e.getMessage(), e);
			throw e;
		}
	}

}
