package com.ezee.food.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ezee.food.controller.io.DishIO;
import com.ezee.food.controller.io.DishLabourIO;
import com.ezee.food.controller.io.LabourIO;
import com.ezee.food.controller.io.ResponseIO;
import com.ezee.food.dto.DishLabourDTO;
import com.ezee.food.service.DishLabourService;

@RestController
@RequestMapping("/dishlabour")
public class DishLabourController {
	@Autowired
	private DishLabourService dishLabour;

	@GetMapping("/")
	public ResponseIO<List<DishLabourIO>> getAllDishLabour(@RequestHeader("authCode") String authCode) {
		List<DishLabourDTO> allDishLabour = dishLabour.getAllDishLabour(authCode);
		List<DishLabourIO> dishLabourList = new ArrayList<DishLabourIO>();
		for (DishLabourDTO dishLabourDTO : allDishLabour) {
			DishLabourIO dishLabIO = new DishLabourIO();
			dishLabIO.setCode(dishLabourDTO.getCode());
			DishIO dishIO = new DishIO();
			dishIO.setCode(dishLabourDTO.getDishDTO().getCode());
			dishIO.setName(dishLabourDTO.getDishDTO().getName());
			dishIO.setTimeToMake(dishLabourDTO.getDishDTO().getTimeToMake());
			dishIO.setMinAvailableQuantity(dishLabourDTO.getDishDTO().getMinAvailableQuantity());
			dishIO.setServingSize(dishLabourDTO.getDishDTO().getServingSize());
			dishLabIO.setDishIO(dishIO);
			LabourIO labour = new LabourIO();
			labour.setCode(dishLabourDTO.getLabourDTO().getCode());
			labour.setName(dishLabourDTO.getLabourDTO().getName());
			labour.setRoleName(dishLabourDTO.getLabourDTO().getRoleName());
			labour.setSpecialization(dishLabourDTO.getLabourDTO().getSpecialization());
			labour.setHourslySalary(dishLabourDTO.getLabourDTO().getHourslySalary());
			dishLabIO.setLabourIO(labour);
			dishLabIO.setHoursRequired(dishLabourDTO.getHoursRequired());
			dishLabourList.add(dishLabIO);
		}
		return ResponseIO.success(dishLabourList);
	}

	@GetMapping("/{code}")
	public ResponseIO<DishLabourIO> getDishLabour(@PathVariable("code") String code,
			@RequestHeader("authCode") String authcode) {
		DishLabourDTO dishLabourDTO = new DishLabourDTO();
		dishLabourDTO.setCode(code);
		DishLabourDTO dishLabourByCode = dishLabour.getDishLabourByCode(dishLabourDTO, authcode);
		DishLabourIO dishLabIO = new DishLabourIO();
		dishLabIO.setCode(dishLabourByCode.getCode());
		DishIO dishIO = new DishIO();
		dishIO.setCode(dishLabourDTO.getDishDTO().getCode());
		dishIO.setName(dishLabourDTO.getDishDTO().getName());
		dishIO.setTimeToMake(dishLabourDTO.getDishDTO().getTimeToMake());
		dishIO.setMinAvailableQuantity(dishLabourDTO.getDishDTO().getMinAvailableQuantity());
		dishIO.setServingSize(dishLabourDTO.getDishDTO().getServingSize());
		dishLabIO.setDishIO(dishIO);
		LabourIO labour = new LabourIO();
		labour.setCode(dishLabourByCode.getLabourDTO().getCode());
		labour.setName(dishLabourByCode.getLabourDTO().getName());
		labour.setRoleName(dishLabourByCode.getLabourDTO().getRoleName());
		labour.setSpecialization(dishLabourByCode.getLabourDTO().getSpecialization());
		labour.setHourslySalary(dishLabourByCode.getLabourDTO().getHourslySalary());
		dishLabIO.setLabourIO(labour);
		dishLabIO.setHoursRequired(dishLabourByCode.getHoursRequired());
		return ResponseIO.success(dishLabIO);
	}

}
