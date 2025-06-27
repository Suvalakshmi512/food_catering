package com.ezee.food.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ezee.food.Exception.ServiceException;
import com.ezee.food.dto.DishDTO;
import com.ezee.food.dto.DishIngredientDTO;
import com.ezee.food.dto.DishLabourDTO;
import com.ezee.food.dto.IngredientDTO;
import com.ezee.food.dto.LabourDTO;
import com.ezee.food.dto.TaxDTO;
import lombok.Cleanup;

@Repository
public class DishDAO {
	@Autowired
	private DataSource dataSource;
	public static final Logger LOGGER = LogManager.getLogger("com.ezee.food.dao");

	public int insertDish(DishDTO dishDTO, Connection connection) {
		int affected = 0;
		try {
			int pindex = 0;
			@Cleanup
			CallableStatement callableStatement = connection
					.prepareCall("{CALL EZEE_SP_DISH_IUD(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

			callableStatement.setString(++pindex, dishDTO.getCode());
			callableStatement.setString(++pindex, dishDTO.getName());
			callableStatement.setString(++pindex, dishDTO.getDescription());
			callableStatement.setInt(++pindex, dishDTO.getTimeToMake());
			callableStatement.setString(++pindex, dishDTO.getVegType());
			callableStatement.setInt(++pindex, dishDTO.getMinAvailableQuantity());
			callableStatement.setInt(++pindex, dishDTO.getServingSize());
			callableStatement.setBigDecimal(++pindex, dishDTO.getMarginProfit());
			callableStatement.setString(++pindex, dishDTO.getTaxDTO().getCode());
			callableStatement.setInt(++pindex, dishDTO.getActiveFlag());
			callableStatement.setString(++pindex, dishDTO.getUpdatedby());

			callableStatement.registerOutParameter(++pindex, Types.INTEGER);
			callableStatement.registerOutParameter(++pindex, Types.INTEGER);

			callableStatement.execute();

			affected = callableStatement.getInt(pindex - 1);
			int insertedId = callableStatement.getInt(pindex);

			LOGGER.info("Rows affected by addDish: {}, Inserted/Updated Dish ID: {}", affected, insertedId);

			dishDTO.setId(insertedId);

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}
		return affected;
	}

	public DishDTO getDish(DishDTO dishDTO) {
		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			@Cleanup
			PreparedStatement dishStmt = null;
			@Cleanup
			PreparedStatement dishIngredientStmt = connection.prepareStatement(
					"SELECT id, code, dish_id, ingredient_id, quantity_used, wastage, price, active_flag FROM dish_ingredient WHERE dish_id = ? AND active_flag = 1");
			@Cleanup
			PreparedStatement dishLabourStmt = connection.prepareStatement(
					"SELECT id, code, dish_id, labour_id, hours_required, active_flag FROM dish_labour WHERE dish_id = ? AND active_flag = 1");

			int dishId = dishDTO.getId();

			if (dishId != 0) {
				String query = "SELECT id, code, name, description, time_to_make_min, veg_type, min_available_qty, serving_size, margin_profit, tax_id, price, active_flag FROM dish WHERE id = ? AND active_flag = 1";
				dishStmt = connection.prepareStatement(query);
				dishStmt.setInt(1, dishId);
			} else {
				String query = "SELECT id, code, name, description, time_to_make_min, veg_type, min_available_qty, serving_size, margin_profit, tax_id, price, active_flag FROM dish WHERE code = ? AND active_flag = 1";
				dishStmt = connection.prepareStatement(query);
				dishStmt.setString(1, dishDTO.getCode());
			}

			@Cleanup
			ResultSet resultSet = dishStmt.executeQuery();

			if (resultSet.next()) {
				dishDTO.setId(resultSet.getInt("id"));
				dishDTO.setCode(resultSet.getString("code"));
				dishDTO.setName(resultSet.getString("name"));
				dishDTO.setDescription(resultSet.getString("description"));
				dishDTO.setTimeToMake(resultSet.getInt("time_to_make_min"));
				dishDTO.setVegType(resultSet.getString("veg_type"));
				dishDTO.setMinAvailableQuantity(resultSet.getInt("min_available_qty"));
				dishDTO.setServingSize(resultSet.getInt("serving_size"));
				dishDTO.setMarginProfit(resultSet.getBigDecimal("margin_profit"));
				dishDTO.setPrice(resultSet.getBigDecimal("price"));
				dishDTO.setActiveFlag(resultSet.getInt("active_flag"));

				int taxId = resultSet.getInt("tax_id");
				TaxDTO tax = new TaxDTO();
				tax.setId(taxId);
				dishDTO.setTaxDTO(tax);
				dishIngredientStmt.setInt(1, dishDTO.getId());
				@Cleanup
				ResultSet dishIng = dishIngredientStmt.executeQuery();
				List<DishIngredientDTO> dishIngList = new ArrayList<>();

				while (dishIng.next()) {
					DishIngredientDTO dishIngredientDTO = new DishIngredientDTO();
					dishIngredientDTO.setId(dishIng.getInt("id"));
					dishIngredientDTO.setCode(dishIng.getString("code"));

					IngredientDTO ingredient = new IngredientDTO();
					ingredient.setId(dishIng.getInt("ingredient_id"));
					dishIngredientDTO.setIngredientDTO(ingredient);

					dishIngredientDTO.setQunatityUsed(dishIng.getBigDecimal("quantity_used"));
					dishIngredientDTO.setWastage(dishIng.getBigDecimal("wastage"));
					dishIngredientDTO.setPrice(dishIng.getBigDecimal("price"));
					dishIngredientDTO.setActiveFlag(dishIng.getInt("active_flag"));

					dishIngList.add(dishIngredientDTO);
				}
				dishDTO.setDishIngredientList(dishIngList);
				dishLabourStmt.setInt(1, dishDTO.getId());
				@Cleanup
				ResultSet dishLab = dishLabourStmt.executeQuery();
				List<DishLabourDTO> dishLabourList = new ArrayList<>();

				while (dishLab.next()) {
					DishLabourDTO dishLabourDTO = new DishLabourDTO();
					dishLabourDTO.setId(dishLab.getInt("id"));
					dishLabourDTO.setCode(dishLab.getString("code"));

					LabourDTO labour = new LabourDTO();
					labour.setId(dishLab.getInt("labour_id"));
					dishLabourDTO.setLabourDTO(labour);

					dishLabourDTO.setHoursRequired(dishLab.getBigDecimal("hours_required"));
					dishLabourDTO.setActiveFlag(dishLab.getInt("active_flag"));

					dishLabourList.add(dishLabourDTO);
				}
				dishDTO.setDishLabourList(dishLabourList);
			}

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return dishDTO;
	}

	public List<DishDTO> getAllDish() {
		List<DishDTO> list = new ArrayList<DishDTO>();

		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			String query = "select id, code, name, description, time_to_make_min, veg_type, min_available_qty, serving_size, margin_profit, tax_id, price, active_flag from dish where active_flag = 1";

			@Cleanup
			PreparedStatement statement = connection.prepareStatement(query);

			@Cleanup
			PreparedStatement dishIngredient = connection.prepareStatement(
					"select id, code, dish_id, ingredient_id, quantity_used, wastage, price, active_flag from dish_ingredient where dish_id = ? and active_flag = 1");

			@Cleanup
			PreparedStatement dishLabour = connection.prepareStatement(
					"select id, code, Dish_id, labour_id, hours_required, active_flag from dish_labour where dish_id = ? and active_flag = 1");

			@Cleanup
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				DishDTO dishDTO = new DishDTO();
				dishDTO.setId(resultSet.getInt("id"));
				dishDTO.setCode(resultSet.getString("code"));
				dishDTO.setName(resultSet.getString("name"));
				dishDTO.setDescription(resultSet.getString("description"));
				dishDTO.setTimeToMake(resultSet.getInt("time_to_make_min"));
				dishDTO.setVegType(resultSet.getString("veg_type"));
				dishDTO.setMinAvailableQuantity(resultSet.getInt("min_available_qty"));
				dishDTO.setServingSize(resultSet.getInt("serving_size"));
				dishDTO.setMarginProfit(resultSet.getBigDecimal("margin_profit"));
				dishDTO.setPrice(resultSet.getBigDecimal("price"));
				TaxDTO tax = new TaxDTO();
				tax.setId(resultSet.getInt("tax_id"));
				dishDTO.setTaxDTO(tax);
				dishDTO.setActiveFlag(resultSet.getInt("active_flag"));

				dishIngredient.setInt(1, dishDTO.getId());
				@Cleanup
				ResultSet dishIng = dishIngredient.executeQuery();
				List<DishIngredientDTO> dishIngList = new ArrayList<DishIngredientDTO>();
				while (dishIng.next()) {
					DishIngredientDTO dishIngredientDTO = new DishIngredientDTO();
					dishIngredientDTO.setId(dishIng.getInt("id"));
					dishIngredientDTO.setCode(dishIng.getString("code"));
					IngredientDTO ingredient = new IngredientDTO();
					ingredient.setId(dishIng.getInt("ingredient_id"));
					dishIngredientDTO.setIngredientDTO(ingredient);
					dishIngredientDTO.setQunatityUsed(dishIng.getBigDecimal("quantity_used"));
					dishIngredientDTO.setWastage(dishIng.getBigDecimal("wastage"));
					dishIngredientDTO.setPrice(dishIng.getBigDecimal("price"));
					dishIngredientDTO.setActiveFlag(dishIng.getInt("active_flag"));
					dishIngList.add(dishIngredientDTO);
				}
				dishIngredient.clearParameters();
				dishDTO.setDishIngredientList(dishIngList);
				dishLabour.setInt(1, dishDTO.getId());
				dishLabour.setInt(1, dishDTO.getId());
				@Cleanup
				ResultSet dishLab = dishLabour.executeQuery();
				List<DishLabourDTO> dishLabourList = new ArrayList<DishLabourDTO>();
				while (dishLab.next()) {
					DishLabourDTO dishLabourDTO = new DishLabourDTO();
					dishLabourDTO.setId(dishLab.getInt("id"));
					dishLabourDTO.setCode(dishLab.getString("code"));
					LabourDTO labour = new LabourDTO();
					labour.setId(dishLab.getInt("labour_id"));
					dishLabourDTO.setLabourDTO(labour);
					dishLabourDTO.setHoursRequired(dishLab.getBigDecimal("hours_required"));
					dishLabourDTO.setActiveFlag(dishLab.getInt("active_flag"));
					dishLabourList.add(dishLabourDTO);
				}
				dishDTO.setDishLabourList(dishLabourList);
				list.add(dishDTO);
			}
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return list;
	}

	public int calculateAndUpdateDishPrice(int dishId, Connection connection) {
		String selectSql = "SELECT EZEE_FN_PRICE_FOR_DISH(?)";
		BigDecimal calculatedPrice;

		try (PreparedStatement prepareState = connection.prepareStatement(selectSql)) {
			prepareState.setInt(1, dishId);
			try (ResultSet rs = prepareState.executeQuery()) {
				if (rs.next()) {
					calculatedPrice = rs.getBigDecimal(1);
				} else {
					throw new ServiceException("Failed to calculate dish price using SQL function.");
				}
			}

			String updateSql = "UPDATE dish SET price = ? WHERE id = ?";
			try (PreparedStatement prepareStatement = connection.prepareStatement(updateSql)) {
				prepareStatement.setBigDecimal(1, calculatedPrice);
				prepareStatement.setInt(2, dishId);
				return prepareStatement.executeUpdate();
			}

		} catch (SQLException e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException("Database error: " + e.getMessage(), e);
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException("Unexpected error: " + e.getMessage(), e);
		}
	}
}
