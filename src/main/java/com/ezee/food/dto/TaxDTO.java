package com.ezee.food.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaxDTO extends BaseDTO {
	private String description;
	private BigDecimal ratePercentage;
	private String updatedby;
	private String updatedAt;
}
