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
import com.ezee.food.cache.redis.service.RedisTaxService;
import com.ezee.food.dao.IngredientDAO;
import com.ezee.food.dto.AuthResponseDTO;
import com.ezee.food.dto.IngredientDTO;
import com.ezee.food.dto.TaxDTO;
import com.ezee.food.service.AuthService;
import com.ezee.food.service.IngredientService;
import com.ezee.food.util.CodeGenarator;
import com.ezee.food.util.DTOUtils;

@Service
public class IngredientImp implements IngredientService {
	@Autowired
	private AuthService authService;
	@Autowired
	private IngredientDAO dao;
	@Autowired
	private RedisTaxService taxCache;

	private static final Logger LOGGER = LogManager.getLogger("com.ezee.food.impl");

	@Override
	public List<IngredientDTO> getAllIngredient(String authCode) {
		List<IngredientDTO> list = new ArrayList<IngredientDTO>();
		try {
			authService.validateAuthCode(authCode);
			list = dao.getAllIngredient();
			for (IngredientDTO data : list) {
				TaxDTO tax = taxCache.getTaxFromCache(data.getTaxDTO());
				data.setTaxDTO(tax);
				if (data.getTaxDTO().getId() == 0) {
					throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
				}
			}
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error while getting all Ingredients: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching Ingredients");
		}
		return list;
	}

	@Override
	public IngredientDTO getIngredientByCode(String code, String authCode) {
		IngredientDTO ingredient = new IngredientDTO();
		try {
			authService.validateAuthCode(authCode);
			ingredient.setCode(code);

			ingredient = dao.getIngredient(ingredient);

			if (ingredient.getTaxDTO() == null || ingredient.getTaxDTO().getId() == 0) {
				throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
			}

			TaxDTO fullTaxDTO = taxCache.getTaxFromCache(ingredient.getTaxDTO());
			ingredient.setTaxDTO(fullTaxDTO);
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error while getting Ingredient: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while fetching Ingredient");
		}
		return ingredient;
	}

	@Override
	public void addIngredient(IngredientDTO ingredientDTO, String authCode) {
		try {
			AuthResponseDTO validateAuthCode = authService.validateAuthCode(authCode);
			ingredientDTO.setUpdatedby(validateAuthCode.getUsername());
			ingredientDTO.setCode(CodeGenarator.generateCode("ING", 12));
			dao.addIngredient(ingredientDTO);
		} catch (Exception e) {
			LOGGER.error("Error while adding Ingredient: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error while inserting ingredient");
		}

	}

	@Override
	public void update(Map<String, Object> ingredient, IngredientDTO ingredientDTO, String authCode) {
		AuthResponseDTO validateAuthCode = authService.validateAuthCode(authCode);
		String code = ingredientDTO.getCode();
		if (code == null) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		IngredientDTO dto = dao.getIngredient(ingredientDTO);
		if (dto.getId() == 0) {
			throw new ServiceException(ErrorCode.ID_OR_CODE_NOT_FOUND_EXCEPTION);
		}
		try {
			if (ingredient != null) {
				ingredient.forEach((key, value) -> {
					switch (key) {
					case "name":
						dto.setName((String) value);
						break;
					case "unitQuantity":
						dto.setUnitQuantity((BigDecimal) value);
						break;
					case "unitCost":
						dto.setUnitCost((BigDecimal) value);
						break;
					case "unit":
						dto.setUnit((String) value);
						break;
					case "activeFlag":
						dto.setActiveFlag((int) value);
						break;
					case "taxIO":
						String taxCode = DTOUtils.extractForeignKeyCode(value);
						if (taxCode != null) {
							TaxDTO taxSearchDTO = new TaxDTO();
							taxSearchDTO.setCode(taxCode);
							dto.setTaxDTO(taxSearchDTO);
						}
						break;

					default:
						throw new ServiceException(ErrorCode.KEY_NOT_FOUND_EXCEPTION);
					}
				});
				dto.setUpdatedby(validateAuthCode.getUsername());
				int updatedRows = dao.addIngredient(dto);
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
