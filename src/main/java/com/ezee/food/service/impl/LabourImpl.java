package com.ezee.food.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezee.food.Exception.ErrorCode;
import com.ezee.food.Exception.ServiceException;
import com.ezee.food.dao.LabourDAO;
import com.ezee.food.dto.AuthResponseDTO;
import com.ezee.food.dto.LabourDTO;
import com.ezee.food.service.AuthService;
import com.ezee.food.service.LabourService;
import com.ezee.food.util.CodeGenarator;

@Service
public class LabourImpl implements LabourService {
	@Autowired
	private AuthService authService;
	@Autowired
	private LabourDAO dao;

	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	@Override
	public List<LabourDTO> getAllLabour(String authCode) {
		List<LabourDTO> list = new ArrayList<LabourDTO>();
		try {
			authService.validateAuthCode(authCode);
			list = dao.getAllLabour();
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error while getting all events: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching labours");
		}
		return list;
	}

	@Override
	public LabourDTO getLabourByCode(String code, String authCode) {
		LabourDTO labourDTO = new LabourDTO();
		try {
			authService.validateAuthCode(authCode);
			labourDTO = new LabourDTO();
			labourDTO.setCode(code);
			dao.getLabour(labourDTO);
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error while getting labours: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching labours");
		}
		return labourDTO;

	}

	@Override
	public void addLabour(LabourDTO labourDTO, String authCode) {
		try {
			AuthResponseDTO validateAuthCode = authService.validateAuthCode(authCode);
			labourDTO.setUpdatedby(validateAuthCode.getUsername());
			labourDTO.setCode(CodeGenarator.generateCode("LBR", 12));
			dao.addLabour(labourDTO);
		} catch (Exception e) {
			LOGGER.error("Error while adding labours: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while inserting labours");
		}
	}

	@Override
	public void update(Map<String, Object> labour, LabourDTO labourDTO, String authCode) {
		AuthResponseDTO validateAuthCode = authService.validateAuthCode(authCode);
		String code = labourDTO.getCode();
		if (code == null) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		LabourDTO dto = dao.getLabour(labourDTO);
		if (dto.getId() == 0) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		try {
			if (labour != null) {
				labour.forEach((key, value) -> {
					switch (key) {
					case "name":
						dto.setName((String) value);
						break;
					case "roleName":
						dto.setRoleName((String) value);
						break;
					case "HourslySalary":
						dto.setHourslySalary((BigDecimal) value);
						break;
					case "specialization":
						dto.setSpecialization((String) value);
						break;
					case "activeFlag":
						dto.setActiveFlag((int) value);
						break;
					default:
						throw new ServiceException(ErrorCode.KEY_NOT_FOUND_EXCEPTION);
					}
				});
				dto.setUpdatedby(validateAuthCode.getUsername());
				int updatedRows = dao.addLabour(dto);
				if (updatedRows == 0) {
					throw new ServiceException(ErrorCode.UPDATE_FAILED_EXCEPTION);
				}
			} else {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Unexpected error: {}", e.getMessage(), e);
			throw e;
		}
	}

}
