package com.ezee.food.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ezee.food.Exception.ServiceException;
import com.ezee.food.controller.io.DishIO;
import com.ezee.food.controller.io.DishIngredientIO;
import com.ezee.food.controller.io.DishLabourIO;
import com.ezee.food.controller.io.IngredientIO;
import com.ezee.food.controller.io.LabourIO;
import com.ezee.food.controller.io.ResponseIO;
import com.ezee.food.controller.io.TaxIO;
import com.ezee.food.dto.DishDTO;
import com.ezee.food.dto.DishIngredientDTO;
import com.ezee.food.dto.DishLabourDTO;
import com.ezee.food.dto.IngredientDTO;
import com.ezee.food.dto.LabourDTO;
import com.ezee.food.dto.TaxDTO;
import com.ezee.food.service.DishService;

@RestController
@RequestMapping("/dish")
public class DishController {

	@Autowired
	private DishService dish;

	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	@GetMapping("/")
	public ResponseIO<List<DishIO>> getAllDish(@RequestHeader("authCode") String authCode) {
		List<DishDTO> allDish = dish.getAllDish(authCode);
		List<DishIO> DishList = new ArrayList<>();

		for (DishDTO dishDTO : allDish) {
			DishList.add(mapToDishIO(dishDTO));
		}

		return ResponseIO.success(DishList);
	}

	@GetMapping("/{code}")
	public ResponseIO<DishIO> getIngredient(@PathVariable("code") String code,
			@RequestHeader("authCode") String authCode) {
		DishDTO dishDTO = dish.getDishByCode(code, authCode);
		DishIO dishIO = mapToDishIO(dishDTO);
		return ResponseIO.success(dishIO);
	}

	@PostMapping("/add")
	public ResponseIO<String> addDish(@RequestBody DishIO dishIO, @RequestHeader("authCode") String authCode) {
		try {
			DishDTO dishDTO = mapToDishDTO(dishIO);
			dish.addDish(dishDTO, authCode);
			return ResponseIO.success("Dish inserted successfully");
		} catch (ServiceException se) {
			LOGGER.error("ServiceException while adding dish: {}", se.getMessage());
			return ResponseIO.failure("125", "Failed to insert dish: " + se.getMessage());
		} catch (Exception e) {
			LOGGER.error("Unexpected exception while adding dish", e);
			return ResponseIO.failure("126", "Unexpected error: " + e.getMessage());
		}
	}

	private DishIO mapToDishIO(DishDTO dishDTO) {
		DishIO dishIO = new DishIO();
		dishIO.setCode(dishDTO.getCode());
		dishIO.setName(dishDTO.getName());
		dishIO.setDescription(dishDTO.getDescription());
		dishIO.setTimeToMake(dishDTO.getTimeToMake());
		dishIO.setVegType(dishDTO.getVegType());
		dishIO.setMinAvailableQuantity(dishDTO.getMinAvailableQuantity());
		dishIO.setServingSize(dishDTO.getServingSize());
		dishIO.setMarginProfit(dishDTO.getMarginProfit());
		dishIO.setPrice(dishDTO.getPrice());

		if (dishDTO.getTaxDTO() != null) {
			TaxIO tax = new TaxIO();
			tax.setCode(dishDTO.getTaxDTO().getCode());
			tax.setDescription(dishDTO.getTaxDTO().getDescription());
			tax.setRatePercentage(dishDTO.getTaxDTO().getRatePercentage());
			dishIO.setTaxIO(tax);
		}

		List<DishIngredientIO> dishIngredientIOList = new ArrayList<>();
		for (DishIngredientDTO dishIngredientDTO : dishDTO.getDishIngredientList()) {
			DishIngredientIO ingIO = new DishIngredientIO();
			ingIO.setCode(dishIngredientDTO.getCode());
			if (dishIngredientDTO.getIngredientDTO() != null) {
				IngredientIO ingIOData = new IngredientIO();
				ingIOData.setCode(dishIngredientDTO.getIngredientDTO().getCode());
				ingIOData.setName(dishIngredientDTO.getIngredientDTO().getName());
				ingIO.setIngredient(ingIOData);
			}
			ingIO.setQunatityUsed(dishIngredientDTO.getQunatityUsed());
			ingIO.setWastage(dishIngredientDTO.getWastage());
			ingIO.setPrice(dishIngredientDTO.getPrice());
			dishIngredientIOList.add(ingIO);
		}
		dishIO.setDishIngredient(dishIngredientIOList);

		List<DishLabourIO> dishLabourIOList = new ArrayList<>();
		for (DishLabourDTO dishLabourDTO : dishDTO.getDishLabourList()) {
			DishLabourIO labIO = new DishLabourIO();
			labIO.setCode(dishLabourDTO.getCode());
			if (dishLabourDTO.getLabourDTO() != null) {
				LabourIO labourIO = new LabourIO();
				labourIO.setCode(dishLabourDTO.getLabourDTO().getCode());
				labourIO.setName(dishLabourDTO.getLabourDTO().getName());
				labourIO.setRoleName(dishLabourDTO.getLabourDTO().getRoleName());
				labourIO.setSpecialization(dishLabourDTO.getLabourDTO().getSpecialization());
				labourIO.setHourslySalary(dishLabourDTO.getLabourDTO().getHourslySalary());
				labIO.setLabour(labourIO);
			}
			labIO.setHoursRequired(dishLabourDTO.getHoursRequired());
			dishLabourIOList.add(labIO);
		}
		dishIO.setDishLabour(dishLabourIOList);

		return dishIO;
	}

	private DishDTO mapToDishDTO(DishIO dishIO) {
		DishDTO dishDTO = new DishDTO();
		dishDTO.setName(dishIO.getName());
		dishDTO.setDescription(dishIO.getDescription());
		dishDTO.setTimeToMake(dishIO.getTimeToMake());
		dishDTO.setVegType(dishIO.getVegType());
		dishDTO.setMinAvailableQuantity(dishIO.getMinAvailableQuantity());
		dishDTO.setServingSize(dishIO.getServingSize());
		dishDTO.setMarginProfit(dishIO.getMarginProfit());
		dishDTO.setPrice(dishIO.getPrice());

		if (dishIO.getTaxIO() != null) {
			TaxDTO taxDTO = new TaxDTO();
			taxDTO.setCode(dishIO.getTaxIO().getCode());
			dishDTO.setTaxDTO(taxDTO);
		}

		List<DishIngredientDTO> ingredientDTOList = new ArrayList<>();
		if (dishIO.getDishIngredient() != null) {
			for (DishIngredientIO ingIO : dishIO.getDishIngredient()) {
				DishIngredientDTO ingDTO = new DishIngredientDTO();
				if (ingIO.getIngredient() != null) {
					IngredientDTO ingredientDTO = new IngredientDTO();
					ingredientDTO.setCode(ingIO.getIngredient().getCode());
					ingDTO.setIngredientDTO(ingredientDTO);
				}
				ingDTO.setQunatityUsed(ingIO.getQunatityUsed());
				ingDTO.setWastage(ingIO.getWastage());
				ingredientDTOList.add(ingDTO);
			}
		}
		dishDTO.setDishIngredientList(ingredientDTOList);

		List<DishLabourDTO> labourDTOList = new ArrayList<>();
		if (dishIO.getDishLabour() != null) {
			for (DishLabourIO labIO : dishIO.getDishLabour()) {
				DishLabourDTO labDTO = new DishLabourDTO();
				if (labIO.getLabour() != null) {
					LabourDTO labourDTO = new LabourDTO();
					labourDTO.setCode(labIO.getLabour().getCode());
					labDTO.setLabourDTO(labourDTO);
				}
				labDTO.setHoursRequired(labIO.getHoursRequired());
				labourDTOList.add(labDTO);
			}
		}
		dishDTO.setDishLabourList(labourDTOList);

		return dishDTO;
	}
}