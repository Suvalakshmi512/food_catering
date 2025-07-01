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

import com.ezee.food.controller.io.DishIO;
import com.ezee.food.controller.io.DishIngredientIO;
import com.ezee.food.controller.io.DishLabourIO;
import com.ezee.food.controller.io.DishListIO;
import com.ezee.food.controller.io.EstimateIO;
import com.ezee.food.controller.io.EventIO;
import com.ezee.food.controller.io.IngredientIO;
import com.ezee.food.controller.io.LabourIO;
import com.ezee.food.controller.io.MenuIO;
import com.ezee.food.controller.io.ResponseIO;
import com.ezee.food.controller.io.TaxIO;
import com.ezee.food.controller.io.UserCustomerIO;
import com.ezee.food.dto.DishIngredientDTO;
import com.ezee.food.dto.DishLabourDTO;
import com.ezee.food.dto.DishListDTO;
import com.ezee.food.dto.EstimateDTO;
import com.ezee.food.dto.EventDTO;
import com.ezee.food.dto.MenuDTO;
import com.ezee.food.service.EstimateService;

@RestController
@RequestMapping("/Estimate")
public class EstimateController {

	@Autowired
	private EstimateService estimateService;

	@GetMapping("/{code}")
	public ResponseIO<EstimateIO> getEstimate(@PathVariable("code") String code,
			@RequestHeader("authCode") String authCode) {
		EstimateDTO dto = estimateService.getEstimateByCode(code, authCode);
		return ResponseIO.success(bindingDTOtoIO(dto));
	}

	@PostMapping("/add")
	public ResponseIO<String> addEstimate(@RequestBody EstimateIO estimateIO,
			@RequestHeader("authCode") String authCode) {
		EstimateDTO dto = bindingIOtoDTO(estimateIO);
		estimateService.addEstimate(dto, authCode);
		return ResponseIO.success("Inserted successfully");
	}

	@GetMapping("/")
	public ResponseIO<List<EstimateIO>> getAllEstimate(@RequestHeader("authCode") String authCode) {
		List<EstimateDTO> dto = estimateService.getEstimate(authCode);
		List<EstimateIO> EstimateIO = new ArrayList<>();

		for (EstimateDTO estimate : dto) {
			EstimateIO esti = bindingDTOtoIO(estimate);
			EstimateIO.add(esti);
		}

		return ResponseIO.success(EstimateIO);
	}

	private EstimateIO bindingDTOtoIO(EstimateDTO dto) {
		EstimateIO estimateIO = new EstimateIO();
		estimateIO.setCode(dto.getCode());
		estimateIO.setDiscount(dto.getDiscount());
		estimateIO.setSubTotal(dto.getSubTotal());
		estimateIO.setGrantTotal(dto.getGrantTotal());

		EventIO eventIO = new EventIO();
		eventIO.setCode(dto.getEventDTO().getCode());
		eventIO.setName(dto.getEventDTO().getName());
		eventIO.setEventDate(dto.getEventDTO().getEventDate());
		eventIO.setEventTime(dto.getEventDTO().getEventTime());
		eventIO.setVenue(dto.getEventDTO().getVenue());
		eventIO.setGuestCount(dto.getEventDTO().getGuestCount());

		UserCustomerIO customer = new UserCustomerIO();
		customer.setCode(dto.getEventDTO().getCustomerDTO().getCode());
		customer.setName(dto.getEventDTO().getCustomerDTO().getName());
		customer.setMobile(dto.getEventDTO().getCustomerDTO().getMobile());
		customer.setEmail(dto.getEventDTO().getCustomerDTO().getEmail());
		eventIO.setCustomer(customer);
		estimateIO.setEvent(eventIO);

		MenuIO menuIO = new MenuIO();
		menuIO.setCode(dto.getMenuDTO().getCode());
		menuIO.setName(dto.getMenuDTO().getName());
		menuIO.setPrice(dto.getMenuDTO().getPrice());

		List<DishListIO> dishList = new ArrayList<>();
		for (DishListDTO listDTO : dto.getMenuDTO().getDishListDTO()) {
			DishListIO dishListIO = new DishListIO();
			dishListIO.setCode(listDTO.getCode());
			dishListIO.setUnitPrice(listDTO.getUnitPrice());

			DishIO dishIO = new DishIO();
			dishIO.setCode(listDTO.getDishDTO().getCode());
			dishIO.setName(listDTO.getDishDTO().getName());
			dishIO.setDescription(listDTO.getDishDTO().getDescription());
			dishIO.setTimeToMake(listDTO.getDishDTO().getTimeToMake());
			dishIO.setVegType(listDTO.getDishDTO().getVegType());
			dishIO.setMinAvailableQuantity(listDTO.getDishDTO().getMinAvailableQuantity());
			dishIO.setServingSize(listDTO.getDishDTO().getServingSize());
			dishIO.setMarginProfit(listDTO.getDishDTO().getMarginProfit());
			dishIO.setPrice(listDTO.getDishDTO().getPrice());

			if (listDTO.getDishDTO().getTaxDTO() != null) {
				TaxIO tax = new TaxIO();
				tax.setCode(listDTO.getDishDTO().getTaxDTO().getCode());
				tax.setDescription(listDTO.getDishDTO().getTaxDTO().getDescription());
				tax.setRatePercentage(listDTO.getDishDTO().getTaxDTO().getRatePercentage());
				dishIO.setTaxIO(tax);
			}

			List<DishIngredientIO> dishIngredient = new ArrayList<>();
			for (DishIngredientDTO ingDTO : listDTO.getDishDTO().getDishIngredientList()) {
				DishIngredientIO ingIO = new DishIngredientIO();
				ingIO.setCode(ingDTO.getCode());
				ingIO.setQunatityUsed(ingDTO.getQunatityUsed());
				ingIO.setWastage(ingDTO.getWastage());
				ingIO.setPrice(ingDTO.getPrice());

				IngredientIO ingredient = new IngredientIO();
				ingredient.setCode(ingDTO.getIngredientDTO().getCode());
				ingredient.setName(ingDTO.getIngredientDTO().getName());
				ingIO.setIngredient(ingredient);

				dishIngredient.add(ingIO);
			}
			dishIO.setDishIngredient(dishIngredient);

			List<DishLabourIO> dishLabourIO = new ArrayList<>();
			for (DishLabourDTO labDTO : listDTO.getDishDTO().getDishLabourList()) {
				DishLabourIO labIO = new DishLabourIO();
				labIO.setCode(labDTO.getCode());
				labIO.setHoursRequired(labDTO.getHoursRequired());

				LabourIO labour = new LabourIO();
				labour.setCode(labDTO.getLabourDTO().getCode());
				labour.setName(labDTO.getLabourDTO().getName());
				labour.setRoleName(labDTO.getLabourDTO().getRoleName());
				labour.setSpecialization(labDTO.getLabourDTO().getSpecialization());
				labour.setHourslySalary(labDTO.getLabourDTO().getHourslySalary());
				labIO.setLabour(labour);

				dishLabourIO.add(labIO);
			}
			dishIO.setDishLabour(dishLabourIO);

			dishListIO.setDish(dishIO);
			dishList.add(dishListIO);
		}
		menuIO.setDishList(dishList);
		estimateIO.setMenu(menuIO);

		return estimateIO;
	}

	private EstimateDTO bindingIOtoDTO(EstimateIO io) {
		EstimateDTO dto = new EstimateDTO();
		dto.setCode(io.getCode());
		dto.setDiscount(io.getDiscount());

		MenuDTO menu = new MenuDTO();
		menu.setCode(io.getMenu().getCode());
		dto.setMenuDTO(menu);

		EventDTO event = new EventDTO();
		event.setCode(io.getEvent().getCode());
		dto.setEventDTO(event);

		return dto;
	}

}
