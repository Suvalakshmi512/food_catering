package com.ezee.food.service;

import java.util.List;
import java.util.Map;

import com.ezee.food.dto.EventDTO;

public interface EventService {
	public List<EventDTO> getAllEvent(String authCode);

	public EventDTO getEventByCode(String code, String authCode);

	public void addEvent(EventDTO eventDTO, String authCode);

	public void update(Map<String, Object> event, EventDTO eventDTO, String authCode);
}
