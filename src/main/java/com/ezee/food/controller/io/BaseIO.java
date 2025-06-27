package com.ezee.food.controller.io;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseIO {
	private String code;
	private String name;
	private int activeFlag = 1;
}
