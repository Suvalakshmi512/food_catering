package com.ezee.food.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ezee.food.controller.io.EventIO;
import com.ezee.food.controller.io.ResponseIO;
import com.ezee.food.controller.io.UserCustomerIO;
import com.ezee.food.dto.EventDTO;
import com.ezee.food.dto.UserCustomerDTO;
import com.ezee.food.service.EventService;

@RestController
@RequestMapping("/event")
public class EventController {

	@Autowired
	private EventService eventService;

	@GetMapping("/")
	public ResponseIO<List<EventIO>> getAllEvent(@RequestHeader("authCode") String authCode) {
		List<EventDTO> allEvent = (List<EventDTO>) eventService.getAllEvent(authCode);
		List<EventIO> eventList = new ArrayList<EventIO>();
		for (EventDTO event : allEvent) {
			EventIO eventIO = new EventIO();
			eventIO.setCode(event.getCode());
			eventIO.setName(event.getName());
			eventIO.setEventDate(event.getEventDate());
			eventIO.setEventTime(event.getEventTime());
			eventIO.setVenue(event.getVenue());
			eventIO.setGuestCount(event.getGuestCount());

			UserCustomerIO customer = new UserCustomerIO();
			customer.setCode(event.getCustomerDTO().getCode());
			customer.setName(event.getCustomerDTO().getName());
			customer.setMobile(event.getCustomerDTO().getMobile());
			customer.setEmail(event.getCustomerDTO().getEmail());
			eventIO.setCustomer(customer);
			eventList.add(eventIO);
		}
		return ResponseIO.success(eventList);
	}

	@GetMapping("/{code}")
	public ResponseIO<EventIO> getEvent(@PathVariable("code") String code, @RequestHeader("authCode") String authcode) {
		EventDTO eventDTO = eventService.getEventByCode(code, authcode);
		EventIO eventIO = new EventIO();
		eventIO.setCode(eventDTO.getCode());
		eventIO.setName(eventDTO.getName());
		eventIO.setEventDate(eventDTO.getEventDate());
		eventIO.setEventTime(eventDTO.getEventTime());
		eventIO.setVenue(eventDTO.getVenue());
		eventIO.setGuestCount(eventDTO.getGuestCount());

		UserCustomerIO customer = new UserCustomerIO();
		customer.setCode(eventDTO.getCustomerDTO().getCode());
		customer.setName(eventDTO.getCustomerDTO().getName());
		customer.setMobile(eventDTO.getCustomerDTO().getMobile());
		customer.setEmail(eventDTO.getCustomerDTO().getEmail());
		eventIO.setCustomer(customer);
		return ResponseIO.success(eventIO);
	}

	@PostMapping("/add")
	public ResponseIO<String> addEvent(@RequestBody EventIO eventIO, @RequestHeader("authCode") String authcode) {
		EventDTO eventDTO = new EventDTO();
		eventDTO.setCode(eventIO.getCode());
		eventDTO.setName(eventIO.getName());
		eventDTO.setEventDate(eventIO.getEventDate());
		eventDTO.setEventTime(eventIO.getEventTime());
		eventDTO.setVenue(eventIO.getVenue());
		eventDTO.setGuestCount(eventIO.getGuestCount());
		UserCustomerDTO customer = new UserCustomerDTO();
		customer.setCode(eventIO.getCustomer().getCode());
		eventDTO.setCustomerDTO(customer);
		eventService.addEvent(eventDTO, authcode);
		return ResponseIO.success("Inserted Successfully");
	}

	@PostMapping("/{code}/update")
	public ResponseIO<String> updateEvent(@RequestBody Map<String, Object> eventMap, @PathVariable("code") String code,
			@RequestHeader("authCode") String authCode) {
		EventDTO eventDTO = new EventDTO();
		eventDTO.setCode(code);

		eventService.update(eventMap, eventDTO, authCode);

		return ResponseIO.success("Event updated successfully");
	}
}
