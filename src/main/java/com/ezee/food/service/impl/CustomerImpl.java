package com.ezee.food.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezee.food.Exception.ErrorCode;
import com.ezee.food.Exception.ServiceException;
import com.ezee.food.dao.UserCustomerDAO;
import com.ezee.food.dto.AuthResponseDTO;
import com.ezee.food.dto.UserCustomerDTO;
import com.ezee.food.service.AuthService;
import com.ezee.food.service.CustomerService;
import com.ezee.food.util.CodeGenarator;

@Service
public class CustomerImpl implements CustomerService {
	@Autowired
	private AuthService authService;
	@Autowired
	private UserCustomerDAO userCustomerDAO;

	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	@Override
	public List<UserCustomerDTO> getAllCustomer(String authCode) {
		List<UserCustomerDTO> list = new ArrayList<UserCustomerDTO>();
		try {
		authService.validateAuthCode(authCode);
	    list = userCustomerDAO.getAllCustomer();
		return list;
		}catch (Exception e) {
			LOGGER.error("Error while getting all Customer: {}", e.getMessage(), e);
	        e.printStackTrace();
	    }
		return list;
		
	}

	@Override
	public UserCustomerDTO getCustomerByCode(String code, String authCode) {
		UserCustomerDTO customerDTO = new UserCustomerDTO();
		try {
		authService.validateAuthCode(authCode);
		customerDTO.setCode(code);
		customerDTO = userCustomerDAO.getCustomer(customerDTO);
		} catch (Exception e) {
			LOGGER.error("Error while getting Customer: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching Customer");
		}
		return customerDTO;
	}

	@Override
	public UserCustomerDTO addCustomer(UserCustomerDTO customerDTO, String authCode) {
		try {
		AuthResponseDTO validateAuthCode = authService.validateAuthCode(authCode);
		customerDTO.setUpdatedBy(validateAuthCode.getUsername());
		customerDTO.setCode(CodeGenarator.generateCode("UCT", 12));
		userCustomerDAO.addCustomer(customerDTO);
		} catch (Exception e) {
			LOGGER.error("Error while adding Customer: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while adding Customer");
		}
		return customerDTO;

	}

	@Override
	public void update(Map<String, Object> customer, UserCustomerDTO customerDTO, String authCode) {
		AuthResponseDTO validateAuthCode = authService.validateAuthCode(authCode);
		String code = customerDTO.getCode();
		if (code == null) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		UserCustomerDTO dto = userCustomerDAO.getCustomer(customerDTO);
		if (dto.getId() == 0) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		try {
			if (customer != null) {
				customer.forEach((key, value) -> {
					switch (key) {
					case "name":
						dto.setName((String) value);
						break;
					case "email":
						dto.setEmail((String) value);
						break;
					case "mobile":
						dto.setMobile((String) value);
						break;
					case "activeFlag":
						dto.setActiveFlag((int) value);
						break;
					default:
						throw new ServiceException(ErrorCode.KEY_NOT_FOUND_EXCEPTION);
					}
				});
				customerDTO.setUpdatedBy(validateAuthCode.getUsername());
				int updatedRows = userCustomerDAO.addCustomer(dto);
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
