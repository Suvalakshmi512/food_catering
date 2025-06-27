package com.ezee.food.Exception;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 7737856834032610669L;

	private Object data;

	private ErrorCode errorCode;


	public ServiceException() {

	}
	public ServiceException(Integer errorCode) {
		this(ErrorCode.getErrorCode(errorCode.toString()), null);
	}

	
	public ServiceException(String message) {
		this(message, null);
	}

	public ServiceException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	
	public ServiceException(ErrorCode errorCode, Object data) {
		this.errorCode = errorCode;
		this.data = data;
	}

	
	public ServiceException(String message, Object data) {
		super(message);
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public String toString() {
		if (errorCode != null) {
			return errorCode.getCode() + "-" + errorCode.getMessage();
		}
		return null;
	}

}


