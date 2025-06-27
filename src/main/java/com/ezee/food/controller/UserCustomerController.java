package com.ezee.food.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ezee.food.controller.io.ResponseIO;
import com.ezee.food.controller.io.UserCustomerIO;
import com.ezee.food.dto.UserCustomerDTO;
import com.ezee.food.service.CustomerService;

@RestController
@RequestMapping("/usercustomer")
public class UserCustomerController {
	@Autowired
	private CustomerService customer;

	@GetMapping("/")
	public ResponseIO<List<UserCustomerIO>> getAllCustomer(@RequestHeader("authCode") String authCode) {
		List<UserCustomerDTO> allCustomer = (List<UserCustomerDTO>) customer.getAllCustomer(authCode);
		List<UserCustomerIO> customer = new ArrayList<UserCustomerIO>();
		for (UserCustomerDTO userCustomerDTO : allCustomer) {
			UserCustomerIO customerIO = new UserCustomerIO();
			customerIO.setCode(userCustomerDTO.getCode());
			customerIO.setName(userCustomerDTO.getName());
			customerIO.setMobile(userCustomerDTO.getMobile());
			customerIO.setEmail(userCustomerDTO.getEmail());
			customer.add(customerIO);
		}
		return ResponseIO.success(customer);
	}

	@GetMapping("/{code}")
	public ResponseIO<UserCustomerIO> getCustomer(@PathVariable("code") String code,
			@RequestHeader("authCode") String authcode) {
		UserCustomerDTO customerDTO = customer.getCustomerByCode(code, authcode);
		UserCustomerIO customerIO = new UserCustomerIO();
		customerIO.setCode(customerDTO.getCode());
		customerIO.setName(customerDTO.getName());
		customerIO.setMobile(customerDTO.getMobile());
		customerIO.setEmail(customerDTO.getEmail());
		return ResponseIO.success(customerIO);
	}

	@PostMapping("/add")
	public ResponseIO<String> addCustomer(@RequestBody UserCustomerIO customerIO,
			@RequestHeader("authCode") String authcode) {
		UserCustomerDTO userCustomer = new UserCustomerDTO();
		userCustomer.setCode(customerIO.getCode());
		userCustomer.setName(customerIO.getName());
		userCustomer.setMobile(customerIO.getMobile());
		userCustomer.setEmail(customerIO.getEmail());
		customer.addCustomer(userCustomer, authcode);
		return ResponseIO.success("Inserted Successfully");
	}

	@PostMapping("/{code}/update")
	public ResponseIO<String> updateCustomer(@RequestBody Map<String, Object> customerUpdate,
			@PathVariable("code") String code, @RequestHeader("authCode") String authCode) {
		UserCustomerDTO customerDTO = new UserCustomerDTO();
		customerDTO.setCode(code);

		customer.update(customerUpdate, customerDTO, authCode);

		return ResponseIO.success("Updated Successfully");
	}

}