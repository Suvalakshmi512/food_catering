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
import com.ezee.food.controller.io.DishIngredientIO;
import com.ezee.food.controller.io.IngredientIO;
import com.ezee.food.controller.io.ResponseIO;
import com.ezee.food.dto.DishIngredientDTO;
import com.ezee.food.service.DishIngredientService;

@RestController
@RequestMapping("/dishingredient")
public class DishIngredientController {
	@Autowired
	private DishIngredientService dishIngredient;

	@GetMapping("/")
	public ResponseIO<List<DishIngredientIO>> getAllDishIngredient(@RequestHeader("authCode") String authCode) {
		List<DishIngredientDTO> allDishIngredient = (List<DishIngredientDTO>) dishIngredient
				.getAllDishIngredient(authCode);
		List<DishIngredientIO> dishIngredient = new ArrayList<DishIngredientIO>();
		for (DishIngredientDTO dishIngredientDTO : allDishIngredient) {
			DishIngredientIO dishIngIO = new DishIngredientIO();
			dishIngIO.setCode(dishIngredientDTO.getCode());
			DishIO dishIO = new DishIO();
			dishIO.setCode(dishIngredientDTO.getDishDTO().getCode());
			dishIO.setName(dishIngredientDTO.getDishDTO().getName());
			dishIO.setTimeToMake(dishIngredientDTO.getDishDTO().getTimeToMake());
			dishIO.setMinAvailableQuantity(dishIngredientDTO.getDishDTO().getMinAvailableQuantity());
			dishIO.setServingSize(dishIngredientDTO.getDishDTO().getServingSize());
			dishIngIO.setDishIO(dishIO);
			IngredientIO ingredient = new IngredientIO();
			ingredient.setCode(dishIngredientDTO.getIngredientDTO().getCode());
			ingredient.setName(dishIngredientDTO.getIngredientDTO().getName());
			dishIngIO.setIngredient(ingredient);
			dishIngIO.setQunatityUsed(dishIngredientDTO.getQunatityUsed());
			dishIngIO.setWastage(dishIngredientDTO.getWastage());
			dishIngIO.setPrice(dishIngredientDTO.getPrice());
			dishIngredient.add(dishIngIO);
		}
		return ResponseIO.success(dishIngredient);
	}

	@GetMapping("/{code}")
	public ResponseIO<DishIngredientIO> getDishIngredient(@PathVariable("code") String code,
			@RequestHeader("authCode") String authcode) {
		DishIngredientDTO dishIngredientDTO = new DishIngredientDTO();
		dishIngredientDTO.setCode(code);
		DishIngredientDTO DishIngredientByCode = dishIngredient.getDishIngredientByCode(dishIngredientDTO, authcode);
		DishIngredientIO dishIngIO = new DishIngredientIO();
		dishIngIO.setCode(DishIngredientByCode.getCode());
		DishIO dishIO = new DishIO();
		dishIO.setCode(dishIngredientDTO.getDishDTO().getCode());
		dishIO.setName(dishIngredientDTO.getDishDTO().getName());
		dishIO.setTimeToMake(dishIngredientDTO.getDishDTO().getTimeToMake());
		dishIO.setMinAvailableQuantity(dishIngredientDTO.getDishDTO().getMinAvailableQuantity());
		dishIO.setServingSize(dishIngredientDTO.getDishDTO().getServingSize());
		dishIngIO.setDishIO(dishIO);
		IngredientIO ingredient = new IngredientIO();
		ingredient.setCode(DishIngredientByCode.getIngredientDTO().getCode());
		ingredient.setName(DishIngredientByCode.getIngredientDTO().getName());
		dishIngIO.setIngredient(ingredient);
		dishIngIO.setQunatityUsed(DishIngredientByCode.getQunatityUsed());
		dishIngIO.setWastage(DishIngredientByCode.getWastage());
		dishIngIO.setPrice(DishIngredientByCode.getPrice());
		return ResponseIO.success(dishIngIO);
	}

}
