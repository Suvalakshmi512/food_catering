package com.ezee.food.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezee.food.Exception.ErrorCode;
import com.ezee.food.Exception.ServiceException;
import com.ezee.food.dao.TaxDAO;
import com.ezee.food.dto.AuthResponseDTO;
import com.ezee.food.dto.TaxDTO;
import com.ezee.food.service.AuthService;
import com.ezee.food.service.TaxService;
import com.ezee.food.util.CodeGenarator;

@Service
public class TaxImpl implements TaxService{
	@Autowired
	private AuthService authService;
	@Autowired
	private TaxDAO dao;
	
	private static final Logger LOGGER=LogManager.getLogger("com.ezee.food.impl");

	@Override
	public List<TaxDTO> getAllTax(String authCode) {
		authService.validateAuthCode(authCode);
		List<TaxDTO> list=dao.getAllTax();
		return list;
	}

	@Override
	public TaxDTO getTaxByCode(String code, String authCode) {
		authService.validateAuthCode(authCode);
		TaxDTO tax = new TaxDTO();
		tax.setCode(code);
		return dao.getTax(tax);
	}

	@Override
	public void addTax(TaxDTO taxDTO, String authCode) {
		AuthResponseDTO validateAuthCode = authService.validateAuthCode(authCode);
		taxDTO.setCode(CodeGenarator.generateCode("TAX", 12));
		taxDTO.setUpdatedby(validateAuthCode.getUsername());
		dao.addTax(taxDTO);
	}
	
	@Override
	public void update(Map<String, Object> tax, TaxDTO taxDTO, String authCode) {
		AuthResponseDTO validateAuthCode = authService.validateAuthCode(authCode);
		String code = taxDTO.getCode();
		if(code == null) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		TaxDTO dto= dao.getTax(taxDTO);
		if(dto.getId()==0) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		try {
			if (tax != null) {
				tax.forEach((key, value) -> {
					switch (key) {
					case "description":
						dto.setDescription((String) value);
						break;
					case "ratePercentage":
						dto.setRatePercentage((BigDecimal) value);
						break;
					case "activeFlag":
						dto.setActiveFlag((int) value);
						break;
					default:
						throw new ServiceException(ErrorCode.KEY_NOT_FOUND_EXCEPTION);
					}
				});
				dto.setUpdatedby(validateAuthCode.getUsername());
				int updatedRows = dao.addTax(dto);
				if (updatedRows == 0) {
					throw new ServiceException(ErrorCode.UPDATE_FAILED_EXCEPTION);
				}
			} else {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}
			}catch (ServiceException e) {
				throw e;
			} catch (Exception e) {
				LOGGER.error("Unexpected error: {}", e.getMessage(), e);
				throw e;
			}
	}
}