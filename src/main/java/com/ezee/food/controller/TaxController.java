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
import com.ezee.food.controller.io.TaxIO;
import com.ezee.food.dto.TaxDTO;
import com.ezee.food.service.TaxService;

@RestController
@RequestMapping("/tax")
public class TaxController {
	@Autowired
	private TaxService taxService;

	@GetMapping("/")
	public ResponseIO<List<TaxIO>> getAllTax(@RequestHeader("authCode") String authCode) {
		List<TaxDTO> allTax = (List<TaxDTO>) taxService.getAllTax(authCode);
		List<TaxIO> tax = new ArrayList<TaxIO>();
		for (TaxDTO taxDTO : allTax) {
			TaxIO taxIO = new TaxIO();
			taxIO.setCode(taxDTO.getCode());
			taxIO.setDescription(taxDTO.getDescription());
			taxIO.setRatePercentage(taxDTO.getRatePercentage());
			tax.add(taxIO);
		}
		return ResponseIO.success(tax);
	}

	@GetMapping("/{code}")
	public ResponseIO<TaxIO> getTax(@PathVariable("code") String code, @RequestHeader("authCode") String authcode) {
		TaxDTO taxDTO = taxService.getTaxByCode(code, authcode);
		TaxIO taxIO = new TaxIO();
		taxIO.setCode(taxDTO.getCode());
		taxIO.setDescription(taxDTO.getDescription());
		taxIO.setRatePercentage(taxDTO.getRatePercentage());
		return ResponseIO.success(taxIO);
	}

	@PostMapping("/add")
	public ResponseIO<String> addTax(@RequestBody TaxIO taxIO, @RequestHeader("authCode") String authcode) {
		TaxDTO tax = new TaxDTO();
		tax.setCode(taxIO.getCode());
		tax.setDescription(taxIO.getDescription());
		tax.setRatePercentage(taxIO.getRatePercentage());
		taxService.addTax(tax, authcode);
		return ResponseIO.success("Inserted Successfully");
	}

	@PostMapping("/{code}/update")
	public ResponseIO<String> updateTax(@RequestBody Map<String, Object> taxUpdate, @PathVariable("code") String code,
			@RequestHeader("authCode") String authCode) {
		TaxDTO taxDTO = new TaxDTO();
		taxDTO.setCode(code);

		taxService.update(taxUpdate, taxDTO, authCode);

		return ResponseIO.success("Updated Successfully");
	}

}
