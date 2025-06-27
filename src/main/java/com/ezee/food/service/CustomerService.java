package com.ezee.food.service;

import java.util.List;
import java.util.Map;

import com.ezee.food.dto.UserCustomerDTO;

public interface CustomerService {
	public List<UserCustomerDTO> getAllCustomer(String authCode);

	public UserCustomerDTO getCustomerByCode(String code, String authCode);

	public UserCustomerDTO addCustomer(UserCustomerDTO customerDTO, String authCode);

	public void update(Map<String, Object> customer, UserCustomerDTO taxDTO, String authCode);

}
