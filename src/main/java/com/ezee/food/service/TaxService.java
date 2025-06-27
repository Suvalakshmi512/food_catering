package com.ezee.food.service;

import java.util.List;
import java.util.Map;

import com.ezee.food.dto.TaxDTO;

public interface TaxService {
	
	public List<TaxDTO> getAllTax(String authCode);
	
	public TaxDTO getTaxByCode(String code,String authCode);
	
	public void addTax(TaxDTO taxDTO,String authCode);
	
	public void update(Map<String, Object> tax,TaxDTO taxDTO,String authCode);
}
