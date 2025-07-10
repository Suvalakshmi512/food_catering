package com.ezee.food.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class CodeGenarator {

	public static String dateTimeFormat(LocalDateTime localDateTime) {
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		return localDateTime.format(pattern);
	}

	public static String generateCode(String prifix, int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
		Random random = new Random();

		StringBuilder code = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			code.append(chars.charAt(random.nextInt(chars.length())));
		}
		return prifix + code.toString();
	}

	public static String generateAuthToken() {
		UUID uuid = UUID.randomUUID();
		String authToken = uuid.toString();
		return authToken;
	}
}
