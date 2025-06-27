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
import com.ezee.food.dto.UserCustomerDTO;

import lombok.Cleanup;
@Repository
public class UserCustomerDAO {
	@Autowired
	private DataSource dataSource;
	
	public static final Logger LOGGER = LogManager.getLogger("com.ezee.food.dao");
	
	public int addCustomer(UserCustomerDTO userCustomerDTO) {
		int affected =0;
		try {
		@Cleanup
		Connection con =dataSource.getConnection();
		int pindex = 0;
		@Cleanup
		CallableStatement callableStatement = con.prepareCall("{CALL EZEE_SP_USER_CUSTOMER_IUD(?, ?, ?, ?, ?, ?, ?)}");
		callableStatement.setString(++pindex, userCustomerDTO.getCode());
		callableStatement.setString(++pindex, userCustomerDTO.getName());
		callableStatement.setString(++pindex, userCustomerDTO.getEmail());
		callableStatement.setString(++pindex, userCustomerDTO.getMobile());
		callableStatement.setInt(++pindex, userCustomerDTO.getActiveFlag());
		callableStatement.setString(++pindex, userCustomerDTO.getUpdatedBy());
		callableStatement.registerOutParameter(++pindex, Types.INTEGER);
		callableStatement.execute();
		affected=callableStatement.getInt("pitRowCount");
		
		}catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}
		return affected;
	}
	public UserCustomerDTO getCustomer(UserCustomerDTO userCustomerDTO) {
		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			@Cleanup
			PreparedStatement statement = null;

			int customeId = userCustomerDTO.getId();

			if (customeId != 0) {
				String query = "select id, code, name, mobile, email, active_flag from user_customer where id = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setInt(1, customeId);
			} else {
				String query = "select id, code, name, mobile, email, active_flag from user_customer where code = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setString(1, userCustomerDTO.getCode());
			}

			@Cleanup
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				userCustomerDTO.setId(resultSet.getInt("id"));
				userCustomerDTO.setCode(resultSet.getString("code"));
				userCustomerDTO.setName(resultSet.getString("name"));
				userCustomerDTO.setMobile(resultSet.getString("mobile"));
				userCustomerDTO.setEmail(resultSet.getString("email"));
				userCustomerDTO.setActiveFlag(resultSet.getInt("active_flag"));
			}

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return userCustomerDTO;
	}
	public List<UserCustomerDTO> getAllCustomer() {
		List<UserCustomerDTO> list = new ArrayList<UserCustomerDTO>();

		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			String query = "select id, code, name, mobile, email, active_flag from user_customer where active_flag = 1";

			@Cleanup
			PreparedStatement statement = connection.prepareStatement(query);

			@Cleanup
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				UserCustomerDTO userCustomerDTO = new UserCustomerDTO();
				userCustomerDTO.setId(resultSet.getInt("id"));
				userCustomerDTO.setCode(resultSet.getString("code"));
				userCustomerDTO.setName(resultSet.getString("name"));
				userCustomerDTO.setMobile(resultSet.getString("mobile"));
				userCustomerDTO.setEmail(resultSet.getString("email"));
				userCustomerDTO.setActiveFlag(resultSet.getInt("active_flag"));

				list.add(userCustomerDTO);
			}

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return list;
	}


		
	}
