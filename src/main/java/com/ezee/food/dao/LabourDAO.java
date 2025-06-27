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
import com.ezee.food.dto.LabourDTO;

import lombok.Cleanup;

@Repository
public class LabourDAO {
	@Autowired
	private DataSource dataSource;
	public static final Logger LOGGER = LogManager.getLogger("com.ezee.food.dao");

	public int addLabour(LabourDTO labourDTO) {
		int affected = 0;
		try {
			@Cleanup
			Connection con = dataSource.getConnection();
			int pindex = 0;
			@Cleanup
			CallableStatement callableStatement = con.prepareCall("{CALL EZEE_SP_LABOUR_IUD(?, ?, ?, ?, ?, ?, ?, ?)}");
			callableStatement.setString(++pindex, labourDTO.getCode());
			callableStatement.setString(++pindex, labourDTO.getName());
			callableStatement.setString(++pindex, labourDTO.getRoleName());
			callableStatement.setBigDecimal(++pindex, labourDTO.getHourslySalary());
			callableStatement.setString(++pindex, labourDTO.getSpecialization());
			callableStatement.setInt(++pindex, labourDTO.getActiveFlag());
			callableStatement.setString(++pindex, labourDTO.getUpdatedby());
			callableStatement.registerOutParameter(++pindex, Types.INTEGER);
			callableStatement.execute();
			affected = callableStatement.getInt("pitRowCount");

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}
		return affected;
	}

	public LabourDTO getLabour(LabourDTO labourDTO) {
		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			@Cleanup
			PreparedStatement statement = null;

			int labourId = labourDTO.getId();

			if (labourId != 0) {
				String query = "select id, code, name, role_name, specialization, hoursly_salary, active_flag from labour where id = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setInt(1, labourId);
			} else {
				String query = "select id, code, name, role_name, specialization, hoursly_salary, active_flag from labour where code = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setString(1, labourDTO.getCode());
			}

			@Cleanup
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				labourDTO.setId(resultSet.getInt("id"));
				labourDTO.setCode(resultSet.getString("code"));
				labourDTO.setName(resultSet.getString("name"));
				labourDTO.setRoleName(resultSet.getString("role_name"));
				labourDTO.setSpecialization(resultSet.getString("specialization"));
				labourDTO.setHourslySalary(resultSet.getBigDecimal("hoursly_salary"));
				labourDTO.setActiveFlag(resultSet.getInt("active_flag"));
			}

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return labourDTO;
	}

	public List<LabourDTO> getAllLabour() {
		List<LabourDTO> list = new ArrayList<LabourDTO>();

		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			String query = "select id, code, name, role_name, specialization, hoursly_salary, active_flag from labour where active_flag = 1";

			@Cleanup
			PreparedStatement statement = connection.prepareStatement(query);

			@Cleanup
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				LabourDTO labourDTO = new LabourDTO();
				labourDTO.setId(resultSet.getInt("id"));
				labourDTO.setCode(resultSet.getString("code"));
				labourDTO.setRoleName(resultSet.getString("role_name"));
				labourDTO.setSpecialization(resultSet.getString("specialization"));
				labourDTO.setHourslySalary(resultSet.getBigDecimal("hoursly_salary"));
				labourDTO.setActiveFlag(resultSet.getInt("active_flag"));

				list.add(labourDTO);
			}

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return list;
	}
}
