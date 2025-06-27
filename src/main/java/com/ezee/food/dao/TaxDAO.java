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
import com.ezee.food.dto.TaxDTO;
import lombok.Cleanup;

@Repository
public class TaxDAO {
	@Autowired
	private DataSource dataSource;
	public static final Logger LOGGER = LogManager.getLogger("com.ezee.food.dao");

	public int addTax(TaxDTO taxDTO) {
		int affected = 0;
		try {
			@Cleanup
			Connection con = dataSource.getConnection();
			int pindex = 0;
			@Cleanup
			CallableStatement callableStatement = con.prepareCall("{CALL EZEE_SP_TAX_IUD(?, ?, ?, ?, ?, ?)}");
			callableStatement.setString(++pindex, taxDTO.getCode());
			callableStatement.setString(++pindex, taxDTO.getDescription());
			callableStatement.setBigDecimal(++pindex, taxDTO.getRatePercentage());
			callableStatement.setInt(++pindex, taxDTO.getActiveFlag());
			callableStatement.setString(++pindex, taxDTO.getUpdatedby());
			callableStatement.registerOutParameter(++pindex, Types.INTEGER);
			callableStatement.execute();
			affected = callableStatement.getInt("pitRowCount");

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}
		return affected;
	}

	public TaxDTO getTax(TaxDTO taxDTO) {
		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			@Cleanup
			PreparedStatement statement = null;

			int taxId = taxDTO.getId();

			if (taxId != 0) {
				String query = "select id, code, description, rate_pct, active_flag from tax where id = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setInt(1, taxId);
			} else {
				String query = "select id, code, description, rate_pct, active_flag from tax where code = ? and active_flag = 1";
				statement = connection.prepareStatement(query);
				statement.setString(1, taxDTO.getCode());
			}

			@Cleanup
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				taxDTO.setId(resultSet.getInt("id"));
				taxDTO.setCode(resultSet.getString("code"));
				taxDTO.setDescription(resultSet.getString("description"));
				taxDTO.setActiveFlag(resultSet.getInt("active_flag"));
				taxDTO.setRatePercentage(resultSet.getBigDecimal("rate_pct"));
			}

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return taxDTO;
	}

	public List<TaxDTO> getAllTax() {
		List<TaxDTO> list = new ArrayList<TaxDTO>();

		try {
			@Cleanup
			Connection connection = dataSource.getConnection();

			String query = "select id, code, description, rate_pct, active_flag from tax where active_flag = 1";

			@Cleanup
			PreparedStatement statement = connection.prepareStatement(query);

			@Cleanup
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				TaxDTO taxDTO = new TaxDTO();
				taxDTO.setId(resultSet.getInt("id"));
				taxDTO.setCode(resultSet.getString("code"));
				taxDTO.setDescription(resultSet.getString("description"));
				taxDTO.setActiveFlag(resultSet.getInt("active_flag"));
				taxDTO.setRatePercentage(resultSet.getBigDecimal("rate_pct"));

				list.add(taxDTO);
			}

		} catch (Exception e) {
			LOGGER.error("\nMessage: {},\nerror: {}", e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

		return list;
	}

}