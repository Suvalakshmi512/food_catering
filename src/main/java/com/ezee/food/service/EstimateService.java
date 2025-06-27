package com.ezee.food.service;

import java.util.List;
import com.ezee.food.dto.EstimateDTO;

public interface EstimateService {
	public EstimateDTO getEstimateByCode(String code, String authCode);
	public List<EstimateDTO> getEstimate(String authCode);

	public void addEstimate(EstimateDTO estimateDTO, String authCode);
}
