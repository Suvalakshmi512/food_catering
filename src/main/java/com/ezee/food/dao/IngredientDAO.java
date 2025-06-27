package com.ezee.food.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ezee.food.Exception.ServiceException;
import com.ezee.food.dto.IngredientDTO;
import com.ezee.food.dto.TaxDTO;
import lombok.Cleanup;

@Repository
public class IngredientDAO {
	@Autowired
	private DataSource dataSource;
	public static final Logger LOGGER = LogManager.getLogger("com.ezee.food.dao");

	public int addIngredient(IngredientDTO ingredientDTO) {
		int affected = 0;
		try {
			@Cleanup
			Connection con = dataSource.getConnection();
			int pindex = 0;
			@Cleanup
			CallableStatement callableStatement = con
					.prepareCall("{CALL EZEE_SP_INGREDIENT_IUD(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			callableStatement.setString(++pindex, ingredientDTO.getCode());
			callableStatement.setString(++pindex, ingredientDTO.getName());
			callableStatement.setBigDecimal(++pindex, ingredientDTO.getUnitQuantity());
			callableStatement.setString(++pindex, ingredientDTO.getUnit());
			callableStatement.setBigDecimal(++pindex, ingredientDTO.getUnitCost());
			callableStatement.setString(++pindex, ingredientDTO.getTaxDTO().getCode());
			callableStatement.setInt(++pindex, ingredientDTO.getActiveFlag());
			callableStatement.setString(++pindex, ingredientDTO.getUpdatedby());
			callableStatement.registerOutParameter(++pindex, Types.INTEGER);
			callableStatement.execute();
			affected = callableStatement.getInt("pitRowCount");

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}
		return affected;
	}

	public IngredientDTO getIngredient(IngredientDTO ingredientDTO) {
		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			@Cleanup
			PreparedStatement statement = null;

			int ingredientId = ingredientDTO.getId();

			if (ingredientId != 0) {
				String query = "select id, code, name, unit_qty, unit, unit_cost, tax_id, active_flag from ingredient where id = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setInt(1, ingredientId);
			} else {
				String query = "select id, code, name, unit_qty, unit, unit_cost, tax_id, active_flag from ingredient where code = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setString(1, ingredientDTO.getCode());
			}
			@Cleanup
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				ingredientDTO.setId(resultSet.getInt("id"));
				ingredientDTO.setCode(resultSet.getString("code"));
				ingredientDTO.setName(resultSet.getString("name"));
				ingredientDTO.setUnitQuantity(resultSet.getBigDecimal("unit_qty"));
				ingredientDTO.setUnit(resultSet.getString("unit"));
				ingredientDTO.setUnitCost(resultSet.getBigDecimal("unit_cost"));
				TaxDTO tax = new TaxDTO();
				tax.setId(resultSet.getInt("tax_id"));
				ingredientDTO.setTaxDTO(tax);
				ingredientDTO.setActiveFlag(resultSet.getInt("active_flag"));

			}
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return ingredientDTO;

	}

	public List<IngredientDTO> getAllIngredient() {
		List<IngredientDTO> list = new ArrayList<IngredientDTO>();

		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			String query = "select id, code, name, unit_qty, unit, unit_cost, tax_id, active_flag from ingredient where active_flag = 1";

			@Cleanup
			PreparedStatement statement = connection.prepareStatement(query);

			@Cleanup
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				IngredientDTO ingredientDTO = new IngredientDTO();
				ingredientDTO.setId(resultSet.getInt("id"));
				ingredientDTO.setCode(resultSet.getString("code"));
				ingredientDTO.setName(resultSet.getString("name"));
				ingredientDTO.setUnitQuantity(resultSet.getBigDecimal("unit_qty"));
				ingredientDTO.setUnit(resultSet.getString("unit"));
				ingredientDTO.setUnitCost(resultSet.getBigDecimal("unit_cost"));
				TaxDTO tax = new TaxDTO();
				tax.setId(resultSet.getInt("tax_id"));
				ingredientDTO.setTaxDTO(tax);
				ingredientDTO.setActiveFlag(resultSet.getInt("active_flag"));
				list.add(ingredientDTO);
			}
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return list;
	}

}
