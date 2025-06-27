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
import com.ezee.food.dto.EstimateDTO;
import com.ezee.food.dto.EventDTO;
import com.ezee.food.dto.MenuDTO;

import lombok.Cleanup;

@Repository
public class EstimateDAO {
	@Autowired
	private DataSource dataSource;
	public static final Logger LOGGER = LogManager.getLogger("com.ezee.food.dao");

	public int addEstimate(EstimateDTO estimateDTO) {
		int affected = 0;
		try {
			@Cleanup
			Connection con = dataSource.getConnection();
			int pindex = 0;
			@Cleanup
			CallableStatement callableStatement = con.prepareCall("{CALL EZEE_SP_ESTIMATE_IUD(?, ?, ?, ?, ?, ?, ?)}");
			callableStatement.setString(++pindex, estimateDTO.getCode());
			callableStatement.setString(++pindex, estimateDTO.getEventDTO().getCode());
			callableStatement.setString(++pindex, estimateDTO.getMenuDTO().getCode());
			callableStatement.setDouble(++pindex, estimateDTO.getDiscount());
			callableStatement.setInt(++pindex, estimateDTO.getActiveFlag());
			callableStatement.setString(++pindex, estimateDTO.getUpdatedBy());
			callableStatement.registerOutParameter(++pindex, Types.INTEGER);
			callableStatement.execute();
			affected = callableStatement.getInt("pitRowCount");

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}
		return affected;
	}

	public EstimateDTO getEstimate(EstimateDTO estimateDTO) {
		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			@Cleanup
			PreparedStatement statement = null;

			int estimateId = estimateDTO.getId();

			if (estimateId != 0) {
				String query = "select id, code, event_id, menu_id, discount, subtotal, grand_total, active_flag from estimate where id = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setInt(1, estimateId);
			} else {
				String query = "select id, code, event_id, menu_id, discount, subtotal, grand_total, active_flag from estimate where code = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setString(1, estimateDTO.getCode());
			}
			@Cleanup
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				estimateDTO.setId(resultSet.getInt("id"));
				estimateDTO.setCode(resultSet.getString("code"));
				EventDTO event = new EventDTO();
				event.setId(resultSet.getInt("event_id"));
				estimateDTO.setEventDTO(event);
				MenuDTO menu = new MenuDTO();
				menu.setId(resultSet.getInt("menu_id"));
				estimateDTO.setMenuDTO(menu);
				estimateDTO.setDiscount(resultSet.getDouble("discount"));
				estimateDTO.setSubTotal(resultSet.getBigDecimal("subtotal"));
				estimateDTO.setGrantTotal(resultSet.getBigDecimal("grand_total"));
				estimateDTO.setActiveFlag(resultSet.getInt("active_flag"));
			}
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return estimateDTO;

	}

	public List<EstimateDTO> getAllEstimate() {
		List<EstimateDTO> list = new ArrayList<EstimateDTO>();

		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			String query = "select id, code, event_id, menu_id, discount, subtotal, grand_total, active_flag from estimate where active_flag = 1";

			@Cleanup
			PreparedStatement statement = connection.prepareStatement(query);

			@Cleanup
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				EstimateDTO estimateDTO = new EstimateDTO();
				estimateDTO.setId(resultSet.getInt("id"));
				estimateDTO.setCode(resultSet.getString("code"));
				EventDTO event = new EventDTO();
				event.setId(resultSet.getInt("event_id"));
				estimateDTO.setEventDTO(event);
				MenuDTO menu = new MenuDTO();
				menu.setId(resultSet.getInt("menu_id"));
				estimateDTO.setMenuDTO(menu);
				estimateDTO.setDiscount(resultSet.getDouble("discount"));
				estimateDTO.setSubTotal(resultSet.getBigDecimal("subtotal"));
				estimateDTO.setGrantTotal(resultSet.getBigDecimal("grand_total"));
				estimateDTO.setActiveFlag(resultSet.getInt("active_flag"));
				list.add(estimateDTO);
			}
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return list;
	}

}
