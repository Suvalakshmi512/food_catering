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
import com.ezee.food.dto.EventDTO;
import com.ezee.food.dto.UserCustomerDTO;
import lombok.Cleanup;

@Repository
public class EventDAO {
	@Autowired
	private DataSource dataSource;
	public static final Logger LOGGER = LogManager.getLogger("com.ezee.food.dao");

	public int addEvent(EventDTO eventDTO) {
		int affected = 0;
		try {
			@Cleanup
			Connection con = dataSource.getConnection();
			int pindex = 0;
			@Cleanup
			CallableStatement callableStatement = con
					.prepareCall("{CALL EZEE_SP_EVENT_IUD(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			callableStatement.setString(++pindex, eventDTO.getCode());
			callableStatement.setString(++pindex, eventDTO.getName());
			callableStatement.setString(++pindex, eventDTO.getCustomerDTO().getCode());
			callableStatement.setString(++pindex, eventDTO.getEventDate());
			callableStatement.setString(++pindex, eventDTO.getEventTime());
			callableStatement.setString(++pindex, eventDTO.getVenue());
			callableStatement.setInt(++pindex, eventDTO.getGuestCount());
			callableStatement.setInt(++pindex, eventDTO.getActiveFlag());
			callableStatement.setString(++pindex, eventDTO.getUpdatedby());
			callableStatement.registerOutParameter(++pindex, Types.INTEGER);
			callableStatement.execute();
			affected = callableStatement.getInt("pitRowCount");

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}
		return affected;
	}

	public EventDTO getEvent(EventDTO dishEventDTO) {
		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			@Cleanup
			PreparedStatement statement = null;

			int eventId = dishEventDTO.getId();

			if (eventId != 0) {
				String query = "select id, code, name, customer_id, event_date, event_time, venue, guest_count, active_flag from event where id = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setInt(1, eventId);
			} else {
				String query = "select id, code, name, customer_id, event_date, event_time, venue, guest_count, active_flag from event where code = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setString(1, dishEventDTO.getCode());
			}
			@Cleanup
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				dishEventDTO.setId(resultSet.getInt("id"));
				dishEventDTO.setCode(resultSet.getString("code"));
				dishEventDTO.setName(resultSet.getString("name"));
				UserCustomerDTO customer = new UserCustomerDTO();
				customer.setId(resultSet.getInt("customer_id"));
				dishEventDTO.setCustomerDTO(customer);
				dishEventDTO.setEventDate(resultSet.getString("event_date"));
				dishEventDTO.setEventTime(resultSet.getString("event_time"));
				dishEventDTO.setVenue(resultSet.getString("venue"));
				dishEventDTO.setGuestCount(resultSet.getInt("guest_count"));
				dishEventDTO.setActiveFlag(resultSet.getInt("active_flag"));

			}
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return dishEventDTO;

	}

	public List<EventDTO> getAllEvent() {
		List<EventDTO> list = new ArrayList<EventDTO>();

		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			String query = "select id, code, name, customer_id, event_date, event_time, venue, guest_count, active_flag from event where active_flag = 1";

			@Cleanup
			PreparedStatement statement = connection.prepareStatement(query);

			@Cleanup
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				EventDTO dishEventDTO = new EventDTO();
				dishEventDTO.setId(resultSet.getInt("id"));
				dishEventDTO.setCode(resultSet.getString("code"));
				dishEventDTO.setName(resultSet.getString("name"));
				UserCustomerDTO customer = new UserCustomerDTO();
				customer.setId(resultSet.getInt("customer_id"));
				dishEventDTO.setCustomerDTO(customer);
				dishEventDTO.setEventDate(resultSet.getString("event_date"));
				dishEventDTO.setEventTime(resultSet.getString("event_time"));
				dishEventDTO.setVenue(resultSet.getString("venue"));
				dishEventDTO.setGuestCount(resultSet.getInt("guest_count"));
				dishEventDTO.setActiveFlag(resultSet.getInt("active_flag"));
				list.add(dishEventDTO);
			}
		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return list;
	}

}
