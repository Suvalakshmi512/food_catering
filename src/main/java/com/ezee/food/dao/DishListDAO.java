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
import com.ezee.food.dto.DishListDTO;
import com.ezee.food.dto.MenuDTO;

import lombok.Cleanup;

@Repository
public class DishListDAO {
	@Autowired
	private DataSource dataSource;
	public static final Logger LOGGER = LogManager.getLogger("com.ezee.food.dao");

	public int addDishList(DishListDTO dishListDTO, Connection con) {
		int affected = 0;
		try {
			int pindex = 0;
			@Cleanup
			CallableStatement callableStatement = con.prepareCall("{CALL EZEE_SP_DISH_LIST_IUD(?, ?, ?, ?, ?, ?)}");
			callableStatement.setString(++pindex, dishListDTO.getCode());
			callableStatement.setString(++pindex, dishListDTO.getMenuDTO().getCode());
			callableStatement.setString(++pindex, dishListDTO.getDishDTO().getCode());
			callableStatement.setInt(++pindex, dishListDTO.getActiveFlag());
			callableStatement.setString(++pindex, dishListDTO.getUpdatedby());
			callableStatement.registerOutParameter(++pindex, Types.INTEGER);
			callableStatement.execute();
			affected = callableStatement.getInt(pindex);

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}
		return affected;
	}

	public DishListDTO getDishList(DishListDTO dishListDTO) {
		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			@Cleanup
			PreparedStatement statement = null;

			int dishListId = dishListDTO.getId();

			if (dishListId != 0) {
				String query = "select id, code, menu_id, dish_id, unit_price, active_flag from dish_list where id = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setInt(1, dishListId);
			} else {
				String query = "select id, code, menu_id, dish_id, unit_price, active_flag from dish_list where code = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setString(1, dishListDTO.getCode());
			}
			@Cleanup
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				dishListDTO.setId(resultSet.getInt("id"));
				dishListDTO.setCode(resultSet.getString("code"));
				DishDTO dish = new DishDTO();
				dish.setId(resultSet.getInt("dish_id"));
				dishListDTO.setDishDTO(dish);
				MenuDTO menu = new MenuDTO();
				menu.setId(resultSet.getInt("menu_id"));
				dishListDTO.setMenuDTO(menu);
				dishListDTO.setUnitPrice(resultSet.getBigDecimal("unit_price"));
				dishListDTO.setActiveFlag(resultSet.getInt("active_flag"));

			}
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return dishListDTO;

	}

	public List<DishListDTO> getAllDishList() {
		List<DishListDTO> list = new ArrayList<DishListDTO>();

		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			String query = "select id, code, menu_id, dish_id, unit_price, active_flag from dish_list where active_flag = 1";

			@Cleanup
			PreparedStatement statement = connection.prepareStatement(query);

			@Cleanup
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				DishListDTO dishListDTO = new DishListDTO();
				dishListDTO.setId(resultSet.getInt("id"));
				dishListDTO.setCode(resultSet.getString("code"));
				DishDTO dish = new DishDTO();
				dish.setId(resultSet.getInt("dish_id"));
				dishListDTO.setDishDTO(dish);
				MenuDTO menu = new MenuDTO();
				menu.setId(resultSet.getInt("menu_id"));
				dishListDTO.setMenuDTO(menu);
				dishListDTO.setUnitPrice(resultSet.getBigDecimal("unit_price"));
				dishListDTO.setActiveFlag(resultSet.getInt("active_flag"));
				list.add(dishListDTO);
			}
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return list;
	}

}
