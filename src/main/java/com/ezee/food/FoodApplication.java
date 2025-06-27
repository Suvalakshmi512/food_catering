package com.ezee.food;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ezee.food.Exception.ServiceException;

@SpringBootApplication
public class FoodApplication {
	public static final Logger LOGGER = LogManager.getLogger("com.ezee.food.dao");

	public static void main(String[] args) {
		try {
			SpringApplication.run(FoodApplication.class, args);
		} catch (Exception e) {
			LOGGER.error("Application failed to start: " + e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
}
