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

import com.ezee.food.controller.io.LabourIO;
import com.ezee.food.controller.io.ResponseIO;
import com.ezee.food.dto.LabourDTO;
import com.ezee.food.service.LabourService;

@RestController
@RequestMapping("/labour")
public class LabourController {
	@Autowired
	private LabourService labour;

	@GetMapping("/")
	public ResponseIO<List<LabourIO>> getAllLabour(@RequestHeader("authCode") String authCode) {
		List<LabourDTO> allLabour = (List<LabourDTO>) labour.getAllLabour(authCode);
		List<LabourIO> labour = new ArrayList<LabourIO>();
		for (LabourDTO labourDTO : allLabour) {
			LabourIO labourIO = new LabourIO();
			labourIO.setCode(labourDTO.getCode());
			labourIO.setName(labourDTO.getName());
			labourIO.setRoleName(labourDTO.getRoleName());
			labourIO.setHourslySalary(labourDTO.getHourslySalary());
			labourIO.setSpecialization(labourDTO.getSpecialization());
			labour.add(labourIO);
		}
		return ResponseIO.success(labour);
	}

	@GetMapping("/{code}")
	public ResponseIO<LabourIO> getLabour(@PathVariable("code") String code,
			@RequestHeader("authCode") String authcode) {
		LabourDTO labourDTO = labour.getLabourByCode(code, authcode);
		LabourIO labourIO = new LabourIO();
		labourIO.setCode(labourDTO.getCode());
		labourIO.setName(labourDTO.getName());
		labourIO.setRoleName(labourDTO.getRoleName());
		labourIO.setHourslySalary(labourDTO.getHourslySalary());
		labourIO.setSpecialization(labourDTO.getSpecialization());
		return ResponseIO.success(labourIO);
	}

	@PostMapping("/add")
	public ResponseIO<String> addLabour(@RequestBody LabourIO labourIO, @RequestHeader("authCode") String authcode) {
		LabourDTO labourDTO = new LabourDTO();
		labourDTO.setCode(labourIO.getCode());
		labourDTO.setName(labourIO.getName());
		labourDTO.setRoleName(labourIO.getRoleName());
		labourDTO.setHourslySalary(labourIO.getHourslySalary());
		labourDTO.setSpecialization(labourIO.getSpecialization());
		labour.addLabour(labourDTO, authcode);
		return ResponseIO.success("Inserted Successfully");
	}

	@PostMapping("/{code}/update")
	public ResponseIO<String> updateLabour(@RequestBody Map<String, Object> labourUpdate,
			@PathVariable("code") String code, @RequestHeader("authCode") String authCode) {
		LabourDTO labourDTO = new LabourDTO();
		labourDTO.setCode(code);

		labour.update(labourUpdate, labourDTO, authCode);

		return ResponseIO.success("Updated Successfully");
	}

}
