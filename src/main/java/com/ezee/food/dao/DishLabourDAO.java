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
import com.ezee.food.dto.DishLabourDTO;
import com.ezee.food.dto.LabourDTO;

import lombok.Cleanup;

@Repository
public class DishLabourDAO {
	@Autowired
	private DataSource dataSource;
	public static final Logger LOGGER = LogManager.getLogger("com.ezee.food.dao");

	public int addDishLabour(DishLabourDTO dishLabourDTO, Connection connection) {
		int affected = 0;
		try {
			int pindex = 0;
			@Cleanup
			CallableStatement callableStatement = connection
					.prepareCall("{CALL EZEE_SP_DISH_LABOUR_IUD(?, ?, ?, ?, ?, ?, ?)}");
			callableStatement.setString(++pindex, dishLabourDTO.getCode());
			callableStatement.setString(++pindex, dishLabourDTO.getDishDTO().getCode());
			callableStatement.setString(++pindex, dishLabourDTO.getLabourDTO().getCode());
			callableStatement.setBigDecimal(++pindex, dishLabourDTO.getHoursRequired());
			callableStatement.setInt(++pindex, dishLabourDTO.getActiveFlag());
			callableStatement.setString(++pindex, dishLabourDTO.getUpdatedby());
			callableStatement.registerOutParameter(++pindex, Types.INTEGER);
			callableStatement.execute();
			LOGGER.info("Labour Insert: code={}, dishCode={}, labourCode={}", dishLabourDTO.getCode(),
					dishLabourDTO.getDishDTO().getCode(), dishLabourDTO.getLabourDTO().getCode());
			affected = callableStatement.getInt("pitRowCount");

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}
		return affected;
	}

	public DishLabourDTO getDishLabour(DishLabourDTO dishLabourDTO) {
		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			@Cleanup
			PreparedStatement statement = null;

			int dishLabourId = dishLabourDTO.getId();

			if (dishLabourId != 0) {
				String query = "select id, code, Dish_id, labour_id, hours_required, active_flag from dish_labour where id = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setInt(1, dishLabourId);
			} else {
				String query = "select id, code, Dish_id, labour_id, hours_required, active_flag from dish_labour where code = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setString(1, dishLabourDTO.getCode());
			}

			@Cleanup
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				dishLabourDTO.setId(resultSet.getInt("id"));
				dishLabourDTO.setCode(resultSet.getString("code"));
				DishDTO dish = new DishDTO();
				dish.setId(resultSet.getInt("dish_id"));
				dishLabourDTO.setDishDTO(dish);
				LabourDTO labour = new LabourDTO();
				labour.setId(resultSet.getInt("labour_id"));
				dishLabourDTO.setLabourDTO(labour);
				dishLabourDTO.setHoursRequired(resultSet.getBigDecimal("hours_required"));
				dishLabourDTO.setActiveFlag(resultSet.getInt("active_flag"));
			}

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return dishLabourDTO;
	}

	public List<DishLabourDTO> getAllDishLabour() {
		List<DishLabourDTO> list = new ArrayList<DishLabourDTO>();

		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			String query = "select id, code, Dish_id, labour_id, hours_required, active_flag from dish_labour where active_flag = 1";

			@Cleanup
			PreparedStatement statement = connection.prepareStatement(query);

			@Cleanup
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				DishLabourDTO dishLabourDTO = new DishLabourDTO();
				dishLabourDTO.setId(resultSet.getInt("id"));
				dishLabourDTO.setCode(resultSet.getString("code"));
				DishDTO dish = new DishDTO();
				dish.setId(resultSet.getInt("dish_id"));
				dishLabourDTO.setDishDTO(dish);
				LabourDTO labour = new LabourDTO();
				labour.setId(resultSet.getInt("labour_id"));
				dishLabourDTO.setLabourDTO(labour);
				dishLabourDTO.setHoursRequired(resultSet.getBigDecimal("hours_required"));
				dishLabourDTO.setActiveFlag(resultSet.getInt("active_flag"));
				list.add(dishLabourDTO);
			}
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}
		return list;
	}
}
