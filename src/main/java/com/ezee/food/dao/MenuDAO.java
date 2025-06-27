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

import lombok.Cleanup;
import com.ezee.food.Exception.ServiceException;
import com.ezee.food.dto.MenuDTO;
import com.ezee.food.dto.DishDTO;
import com.ezee.food.dto.DishListDTO;

@Repository
public class MenuDAO {
	@Autowired
	private DataSource dataSource;
	public static final Logger LOGGER = LogManager.getLogger("com.ezee.food.dao");

	public int insertMenu(MenuDTO manuDTO, Connection con) {
		int affected = 0;
		try {
			int pindex = 0;
			@Cleanup
			CallableStatement callableStatement = con.prepareCall("{CALL EZEE_SP_MENU_IUD(?, ?, ?, ?, ?, ?)}");
			callableStatement.setString(++pindex, manuDTO.getCode());
			callableStatement.setString(++pindex, manuDTO.getName());
			callableStatement.setInt(++pindex, manuDTO.getActiveFlag());
			callableStatement.setString(++pindex, manuDTO.getUpdatedby());

			callableStatement.registerOutParameter(++pindex, Types.INTEGER);
			callableStatement.registerOutParameter(++pindex, Types.INTEGER);
			callableStatement.execute();
			affected = callableStatement.getInt(pindex - 1);
			int insertedId = callableStatement.getInt(pindex);

			LOGGER.info("Rows affected by addMenu: {}, Inserted/Updated Menu ID: {}", affected, insertedId);

			manuDTO.setId(insertedId);

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}
		return affected;
	}

	public MenuDTO getMenu(MenuDTO menuDTO) {
		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			@Cleanup
			PreparedStatement menu = null;

			@Cleanup
			PreparedStatement dishList = connection.prepareStatement(
					"select id, code, menu_id, dish_id, unit_price, active_flag from dish_list where menu_id = ? and active_flag = 1");

			int menuId = menuDTO.getId();

			if (menuId != 0) {
				String query = "select id, code, name, price, active_flag from menu where id = ? and active_flag = 1";
				menu = connection.prepareStatement(query);
				menu.setInt(1, menuId);
			} else {
				String query = "select id, code, name, price, active_flag from menu where code = ? and active_flag = 1";
				menu = connection.prepareStatement(query);
				menu.setString(1, menuDTO.getCode());
			}
			@Cleanup
			ResultSet resultSet = menu.executeQuery();
			while (resultSet.next()) {
				menuDTO.setId(resultSet.getInt("id"));
				menuDTO.setCode(resultSet.getString("code"));
				menuDTO.setName(resultSet.getString("name"));
				menuDTO.setPrice(resultSet.getBigDecimal("price"));
				menuDTO.setActiveFlag(resultSet.getInt("active_flag"));

				dishList.setInt(1, menuDTO.getId());
				@Cleanup
				ResultSet dishLit = dishList.executeQuery();
				List<DishListDTO> distList = new ArrayList<DishListDTO>();
				while (dishLit.next()) {
					DishListDTO dishListDTO = new DishListDTO();
					dishListDTO.setId(dishLit.getInt("id"));
					dishListDTO.setCode(dishLit.getString("code"));
					DishDTO dish = new DishDTO();
					dish.setId(dishLit.getInt("dish_id"));
					dishListDTO.setDishDTO(dish);
					dishListDTO.setUnitPrice(dishLit.getBigDecimal("unit_price"));
					dishListDTO.setActiveFlag(dishLit.getInt("active_flag"));
					distList.add(dishListDTO);
				}
				menuDTO.setDishListDTO(distList);

			}
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return menuDTO;

	}

	public List<MenuDTO> getAllMenu() {
		List<MenuDTO> list = new ArrayList<MenuDTO>();

		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			String query = "select id, code, name, price, active_flag from menu where active_flag = 1";

			@Cleanup
			PreparedStatement statement = connection.prepareStatement(query);
			@Cleanup
			PreparedStatement dishList = connection.prepareStatement(
					"select id, code, menu_id, dish_id, unit_price, active_flag from dish_list where menu_id = ? and active_flag = 1");
			@Cleanup
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				MenuDTO menuDTO = new MenuDTO();
				menuDTO.setId(resultSet.getInt("id"));
				menuDTO.setCode(resultSet.getString("code"));
				menuDTO.setName(resultSet.getString("name"));
				menuDTO.setPrice(resultSet.getBigDecimal("price"));
				menuDTO.setActiveFlag(resultSet.getInt("active_flag"));
				dishList.setInt(1, menuDTO.getId());
				@Cleanup
				ResultSet dishLit = dishList.executeQuery();
				List<DishListDTO> distList = new ArrayList<DishListDTO>();
				while (dishLit.next()) {
					DishListDTO dishListDTO = new DishListDTO();
					dishListDTO.setId(dishLit.getInt("id"));
					dishListDTO.setCode(dishLit.getString("code"));
					DishDTO dish = new DishDTO();
					dish.setId(dishLit.getInt("dish_id"));
					dishListDTO.setDishDTO(dish);
					dishListDTO.setUnitPrice(dishLit.getBigDecimal("unit_price"));
					dishListDTO.setActiveFlag(dishLit.getInt("active_flag"));
					distList.add(dishListDTO);
				}
				menuDTO.setDishListDTO(distList);
				list.add(menuDTO);
			}
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return list;
	}

	public int calculateAndUpdateMenuPrice(int id, Connection connection) {
		String sql = "UPDATE menu SET price = ( SELECT IFNULL(SUM(unit_price), 0) FROM dish_list  WHERE menu_id = ? AND active_flag = '1' ) WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, id);
			stmt.setInt(2, id);
			return stmt.executeUpdate();
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}
	}

}
