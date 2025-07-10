package com.ezee.food.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ezee.food.Exception.ErrorCode;
import com.ezee.food.Exception.ServiceException;
import com.ezee.food.controller.io.DishIO;
import com.ezee.food.controller.io.DishIngredientIO;
import com.ezee.food.controller.io.DishLabourIO;
import com.ezee.food.controller.io.DishListIO;
import com.ezee.food.controller.io.IngredientIO;
import com.ezee.food.controller.io.LabourIO;
import com.ezee.food.controller.io.MenuIO;
import com.ezee.food.controller.io.ResponseIO;
import com.ezee.food.controller.io.TaxIO;
import com.ezee.food.dto.DishDTO;
import com.ezee.food.dto.DishIngredientDTO;
import com.ezee.food.dto.DishLabourDTO;
import com.ezee.food.dto.DishListDTO;
import com.ezee.food.dto.MenuDTO;
import com.ezee.food.service.MenuService;

@RestController
@RequestMapping("/menu")
public class MenuController {
	@Autowired
	private MenuService menu;

	@GetMapping("/")
	public ResponseIO<List<MenuIO>> getAllMenu(@RequestHeader("authCode") String authCode) {
		List<MenuDTO> allMenu = (List<MenuDTO>) menu.getAllMenu(authCode);
		List<MenuIO> menuList = new ArrayList<>();
		for (MenuDTO menuDTO : allMenu) {
			menuList.add(convertToMenuIO(menuDTO));
		}
		return ResponseIO.success(menuList);
	}

	@GetMapping("/{code}")
	public ResponseIO<MenuIO> getMenu(@PathVariable("code") String code, @RequestHeader("authCode") String authCode) {
		MenuDTO menuDTO = menu.getMenuByCode(code, authCode);
		return ResponseIO.success(convertToMenuIO(menuDTO));
	}

	@PostMapping("/insert")
	public ResponseIO<String> addMenu(@RequestBody MenuIO menuIO, @RequestHeader("authCode") String authCode) {
		try {
			MenuDTO menuDTO = convertToMenuDTO(menuIO);
			menu.addMenu(menuDTO, authCode);
			if(menuDTO.getId() == 0) {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}
			menu.updatePrice(menuDTO.getId(), authCode);
		} catch (ServiceException se) {
			 ResponseIO.failure("125", "Failed to insert menu: " + se.getMessage());
		} catch (Exception e) {
			 ResponseIO.failure("126", "Unexpected error: " + e.getMessage());
		}
		return ResponseIO.success("Menu inserted successfully");
	}

	private MenuIO convertToMenuIO(MenuDTO menuDTO) {
		MenuIO menuIO = new MenuIO();
		menuIO.setCode(menuDTO.getCode());
		menuIO.setName(menuDTO.getName());
		menuIO.setPrice(menuDTO.getPrice());

		List<DishListIO> dishList = new ArrayList<>();
		for (DishListDTO dishListDTO : menuDTO.getDishListDTO()) {
			DishListIO dishListIO = new DishListIO();
			dishListIO.setCode(dishListDTO.getCode());
			dishListIO.setDish(convertToDishIO(dishListDTO.getDishDTO()));
			dishListIO.setUnitPrice(dishListDTO.getUnitPrice());
			dishList.add(dishListIO);
		}
		menuIO.setDishList(dishList);
		return menuIO;
	}

	private DishIO convertToDishIO(DishDTO dishDTO) {
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

		TaxIO taxIO = new TaxIO();
		taxIO.setCode(dishDTO.getTaxDTO().getCode());
		taxIO.setDescription(dishDTO.getTaxDTO().getDescription());
		taxIO.setRatePercentage(dishDTO.getTaxDTO().getRatePercentage());
		dishIO.setTaxIO(taxIO);

		List<DishIngredientIO> ingredientList = new ArrayList<>();
		for (DishIngredientDTO ingDTO : dishDTO.getDishIngredientList()) {
			DishIngredientIO ingIO = new DishIngredientIO();
			ingIO.setCode(ingDTO.getCode());

			IngredientIO ingredientIO = new IngredientIO();
			ingredientIO.setCode(ingDTO.getIngredientDTO().getCode());
			ingredientIO.setName(ingDTO.getIngredientDTO().getName());
			ingIO.setIngredient(ingredientIO);

			ingIO.setQunatityUsed(ingDTO.getQunatityUsed());
			ingIO.setWastage(ingDTO.getWastage());
			ingIO.setPrice(ingDTO.getPrice());
			ingredientList.add(ingIO);
		}
		dishIO.setDishIngredient(ingredientList);

		List<DishLabourIO> labourList = new ArrayList<>();
		for (DishLabourDTO labDTO : dishDTO.getDishLabourList()) {
			DishLabourIO labIO = new DishLabourIO();
			labIO.setCode(labDTO.getCode());

			LabourIO labourIO = new LabourIO();
			labourIO.setCode(labDTO.getLabourDTO().getCode());
			labourIO.setName(labDTO.getLabourDTO().getName());
			labourIO.setRoleName(labDTO.getLabourDTO().getRoleName());
			labourIO.setSpecialization(labDTO.getLabourDTO().getSpecialization());
			labourIO.setHourslySalary(labDTO.getLabourDTO().getHourslySalary());

			labIO.setLabour(labourIO);
			labIO.setHoursRequired(labDTO.getHoursRequired());
			labourList.add(labIO);
		}
		dishIO.setDishLabour(labourList);

		return dishIO;
	}
	private MenuDTO convertToMenuDTO(MenuIO menuIO) {
	    MenuDTO menuDTO = new MenuDTO();

	    menuDTO.setCode(menuIO.getCode());
	    menuDTO.setName(menuIO.getName());
	    menuDTO.setPrice(menuIO.getPrice());

	    List<DishListDTO> dishList = new ArrayList<>();
	    if (menuIO.getDishList() != null) {
	        for (DishListIO dishListIO : menuIO.getDishList()) {
	            DishListDTO dishListDTO = new DishListDTO();
	            dishListDTO.setCode(dishListIO.getCode());
	            dishListDTO.setUnitPrice(dishListIO.getUnitPrice());

	            if (dishListIO.getDish() != null) {
	                DishDTO dishDTO = new DishDTO();
	                dishDTO.setCode(dishListIO.getDish().getCode());
	                dishListDTO.setDishDTO(dishDTO);
	            }

	            dishList.add(dishListDTO);
	        }
	    }
	    menuDTO.setDishListDTO(dishList);

	    return menuDTO;
	}
}
