package com.ezee.food.service;

import java.util.List;
import com.ezee.food.dto.MenuDTO;

public interface MenuService {
	public List<MenuDTO> getAllMenu(String authCode);

	public MenuDTO getMenuByCode(String code, String authCode);

	public void addMenu(MenuDTO menuDTO, String authCode);
}