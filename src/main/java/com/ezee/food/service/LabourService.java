package com.ezee.food.service;

import java.util.List;
import java.util.Map;

import com.ezee.food.dto.LabourDTO;

public interface LabourService {
	public List<LabourDTO> getAllLabour(String authCode);

	public LabourDTO getLabourByCode(String code, String authCode);

	public void addLabour(LabourDTO labourDTO, String authCode);

	public void update(Map<String, Object> labour, LabourDTO labourDTO, String authCode);


}
