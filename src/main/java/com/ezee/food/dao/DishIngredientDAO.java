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
import com.ezee.food.dto.DishDTO;
import com.ezee.food.dto.DishIngredientDTO;
import com.ezee.food.dto.IngredientDTO;
import lombok.Cleanup;

@Repository
public class DishIngredientDAO {
	@Autowired
	private DataSource dataSource;
	public static final Logger LOGGER = LogManager.getLogger("com.ezee.food.dao");

	public int addDishIngredient(DishIngredientDTO dishIngredientDTO, Connection connection) {
		int affected = 0;
		try {
			int pindex = 0;
			@Cleanup
			CallableStatement callableStatement = connection
					.prepareCall("{CALL EZEE_SP_DISH_INGREDIENT_IUD(?, ?, ?, ?, ?, ?, ?, ?)}");
			callableStatement.setString(++pindex, dishIngredientDTO.getCode());
			callableStatement.setString(++pindex, dishIngredientDTO.getDishDTO().getCode());
			callableStatement.setString(++pindex, dishIngredientDTO.getIngredientDTO().getCode());
			callableStatement.setBigDecimal(++pindex, dishIngredientDTO.getQunatityUsed());
			callableStatement.setBigDecimal(++pindex, dishIngredientDTO.getWastage());
			callableStatement.setInt(++pindex, dishIngredientDTO.getActiveFlag());
			callableStatement.setString(++pindex, dishIngredientDTO.getUpdatedby());
			callableStatement.registerOutParameter(++pindex, Types.INTEGER);
			callableStatement.execute();
			affected = callableStatement.getInt("pitRowCount");

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}
		return affected;
	}

	public DishIngredientDTO getIngredient(DishIngredientDTO dishIngredientDTO) {
		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			@Cleanup
			PreparedStatement statement = null;

			int ingredientId = dishIngredientDTO.getId();

			if (ingredientId != 0) {
				String query = "select id, code, dish_id, ingredient_id, quantity_used, wastage, price, active_flag from dish_ingredient where id = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setInt(1, ingredientId);
			} else {
				String query = "select id, code, dish_id, ingredient_id, quantity_used, wastage, price, active_flag from dish_ingredient where code = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setString(1, dishIngredientDTO.getCode());
			}
			@Cleanup
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				dishIngredientDTO.setId(resultSet.getInt("id"));
				dishIngredientDTO.setCode(resultSet.getString("code"));
				DishDTO dish = new DishDTO();
				dish.setId(resultSet.getInt("dish_id"));
				dishIngredientDTO.setDishDTO(dish);
				IngredientDTO ingredient = new IngredientDTO();
				ingredient.setId(resultSet.getInt("ingredient_id"));
				dishIngredientDTO.setIngredientDTO(ingredient);
				dishIngredientDTO.setQunatityUsed(resultSet.getBigDecimal("quantity_used"));
				dishIngredientDTO.setWastage(resultSet.getBigDecimal("wastage"));
				dishIngredientDTO.setPrice(resultSet.getBigDecimal("price"));
				dishIngredientDTO.setActiveFlag(resultSet.getInt("active_flag"));

			}
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return dishIngredientDTO;

	}

	public List<DishIngredientDTO> getAllDishIngredient() {
		List<DishIngredientDTO> list = new ArrayList<DishIngredientDTO>();

		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			String query = "select id, code, dish_id, ingredient_id, quantity_used, wastage, price, active_flag from dish_ingredient where active_flag = 1";

			@Cleanup
			PreparedStatement statement = connection.prepareStatement(query);

			@Cleanup
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				DishIngredientDTO dishIngredientDTO = new DishIngredientDTO();
				dishIngredientDTO.setId(resultSet.getInt("id"));
				dishIngredientDTO.setCode(resultSet.getString("code"));
				DishDTO dish = new DishDTO();
				dish.setId(resultSet.getInt("dish_id"));
				dishIngredientDTO.setDishDTO(dish);
				IngredientDTO ingredient = new IngredientDTO();
				ingredient.setId(resultSet.getInt("ingredient_id"));
				dishIngredientDTO.setIngredientDTO(ingredient);
				dishIngredientDTO.setQunatityUsed(resultSet.getBigDecimal("quantity_used"));
				dishIngredientDTO.setWastage(resultSet.getBigDecimal("wastage"));
				dishIngredientDTO.setPrice(resultSet.getBigDecimal("price"));
				dishIngredientDTO.setActiveFlag(resultSet.getInt("active_flag"));
				list.add(dishIngredientDTO);
			}
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return list;
	}

}
