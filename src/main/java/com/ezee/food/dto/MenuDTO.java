package com.ezee.food.dto;



import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MenuDTO extends BaseDTO {
	private List<DishListDTO> dishListDTO;
	private BigDecimal price;
	private String updatedby;
	private String updatedAt;
	

}
