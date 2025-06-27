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

import com.ezee.food.controller.io.IngredientIO;
import com.ezee.food.controller.io.ResponseIO;
import com.ezee.food.controller.io.TaxIO;
import com.ezee.food.dto.IngredientDTO;
import com.ezee.food.dto.TaxDTO;
import com.ezee.food.service.IngredientService;

@RestController
@RequestMapping("/ingredient")
public class IngredientController {
	@Autowired
	private IngredientService ingredient;

	@GetMapping("/")
	public ResponseIO<List<IngredientIO>> getAllIngredient(@RequestHeader("authCode") String authCode) {
		List<IngredientDTO> allIngredient = (List<IngredientDTO>) ingredient.getAllIngredient(authCode);
		List<IngredientIO> ingredient = new ArrayList<IngredientIO>();
		for (IngredientDTO ingredientDTO : allIngredient) {
			IngredientIO ingredientIO = new IngredientIO();
			ingredientIO.setCode(ingredientDTO.getCode());
			ingredientIO.setName(ingredientDTO.getName());
			ingredientIO.setUnitQuantity(ingredientDTO.getUnitQuantity());
			ingredientIO.setUnit(ingredientDTO.getUnit());
			ingredientIO.setUnitCost(ingredientDTO.getUnitCost());

			TaxIO tax = new TaxIO();
			tax.setCode(ingredientDTO.getTaxDTO().getCode());
			tax.setDescription(ingredientDTO.getTaxDTO().getDescription());
			tax.setRatePercentage(ingredientDTO.getTaxDTO().getRatePercentage());
			ingredientIO.setTaxIO(tax);
			ingredient.add(ingredientIO);
		}
		return ResponseIO.success(ingredient);
	}

	@GetMapping("/{code}")
	public ResponseIO<IngredientIO> getIngredient(@PathVariable("code") String code,
			@RequestHeader("authCode") String authcode) {
		IngredientDTO ingredientDTO = ingredient.getIngredientByCode(code, authcode);
		IngredientIO ingredientIO = new IngredientIO();
		ingredientIO.setCode(ingredientDTO.getCode());
		ingredientIO.setName(ingredientDTO.getName());
		ingredientIO.setUnitQuantity(ingredientDTO.getUnitQuantity());
		ingredientIO.setUnit(ingredientDTO.getUnit());
		ingredientIO.setUnitCost(ingredientDTO.getUnitCost());

		TaxIO tax = new TaxIO();
		tax.setCode(ingredientDTO.getTaxDTO().getCode());
		tax.setDescription(ingredientDTO.getTaxDTO().getDescription());
		tax.setRatePercentage(ingredientDTO.getTaxDTO().getRatePercentage());
		ingredientIO.setTaxIO(tax);
		return ResponseIO.success(ingredientIO);
	}

	@PostMapping("/add")
	public ResponseIO<String> addIngredient(@RequestBody IngredientIO ingredientIO,
			@RequestHeader("authCode") String authcode) {
		IngredientDTO ingredientDTO = new IngredientDTO();
		ingredientDTO.setCode(ingredientIO.getCode());
		ingredientDTO.setName(ingredientIO.getName());
		ingredientDTO.setUnitQuantity(ingredientIO.getUnitQuantity());
		ingredientDTO.setUnit(ingredientIO.getUnit());
		ingredientDTO.setUnitCost(ingredientIO.getUnitCost());
		TaxDTO tax = new TaxDTO();
		tax.setCode(ingredientIO.getTaxIO().getCode());
		ingredientDTO.setTaxDTO(tax);
		ingredient.addIngredient(ingredientDTO, authcode);
		return ResponseIO.success("Inserted Successfully");
	}

	@PostMapping("/{code}/update")
	public ResponseIO<String> updateLabour(@RequestBody Map<String, Object> ingredientUpdate,
			@PathVariable("code") String code, @RequestHeader("authCode") String authCode) {
		IngredientDTO ingredientDTO = new IngredientDTO();
		ingredientDTO.setCode(code);

		ingredient.update(ingredientUpdate, ingredientDTO, authCode);

		return ResponseIO.success("Updated Successfully");
	}

}
