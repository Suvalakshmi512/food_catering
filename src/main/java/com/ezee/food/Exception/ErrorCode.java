package com.ezee.food.Exception;

public enum ErrorCode {
	UNDEFINE_EXCEPTION("500", "Un Known Exception"), USER_INVALID_AUTH_TOKEN("100", "Invalid Auth Token"),
	ID_OR_CODE_NOT_FOUND_EXCEPTION("111", "Id or code not Found"), EMPTY_RESULT_DATA_EXCEPTION("112", "list is empty"),
	KEY_NOT_FOUND_EXCEPTION("113", "Key not Found"), EMAIL_FOUND_EXCEPTION("114", "Email already Exists in the database."),
	UPDATE_FAILED_EXCEPTION("115", "Update failed"), DATA_ALREADY_EXISTS("115", "Data Already EXists in the database."),
	DATABASE_ERROR("116", "Database operation failed."), NOT_FOUND("117", "Data Not found"),
	INVALID_DATA_TYPE_EXCEPTION("118", "Invalid Data Type"), INVALID_FOREIGN_KEY("119", "Foreingkey not available"),
	STATUS_NOT_ACCEPTABLE("120", "Authentication service is currently unavailable. Please try again later"),
	USERNAME_PASSWORD_INCORRECT_EXCEPTION("121", "Invalid credentials"), SERVICE_UNAVAILABLE("122", "Auth API error"),
	INVALID_TOKEN("123", "Invalid or expired authCode"), UNAUTHORIZED("124", "Access token is missing or invalid"),
	CACHE_NOT_FOUND_EXCEPTION("125", "Cache not found"),INSERT_FAILED("125", "Insert Operation Failed"),
	UNEXPECTED_ERROR("126", "Unexpected exception");

	private final String code;
	private final String message;

	private ErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String getCode() {
		return code;
	}

	public Integer getIntCode() {
		return Integer.valueOf(code);
	}

	public String toString() {
		return code + ": " + message;
	}

	public static ErrorCode getErrorCode(String value) {
		ErrorCode[] values = values();
		for (ErrorCode errorCode : values) {
			if (errorCode.getCode().equalsIgnoreCase(value)) {
				return errorCode;
			}
		}
		return UNDEFINE_EXCEPTION;
	}

}
