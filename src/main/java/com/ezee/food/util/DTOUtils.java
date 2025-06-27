package com.ezee.food.util;

import java.util.Map;

public class DTOUtils {
	@SuppressWarnings("unchecked")
	public static String extractForeignKeyCode(Object value) {
		if (value instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) value;
			Object code = map.get("code");
			if (code != null) {
				return code.toString();
			}
		}
		return null;
	}
}
