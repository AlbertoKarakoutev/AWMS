package com.company.awms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.awms.services.EmployeeService;
import com.company.awms.services.ScheduleService;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

	private static final boolean active = true;

	private ScheduleService scheduleService;

	@Autowired
	public ScheduleController(ScheduleService scheduleService, EmployeeService employeeService) {
		this.scheduleService = scheduleService;
	}

	@GetMapping("/swap")
	public ResponseEntity<String> swapEmployees(@RequestParam String requestorNationalID, @RequestParam String receiverNationalID, @RequestParam String requestorDate, @RequestParam String receiverDate) {
		boolean success = scheduleService.swapEmployees(requestorNationalID, receiverNationalID, requestorDate, receiverDate);
		if (success) {
			return new ResponseEntity<String>("Successfully swapped " + requestorNationalID + " and " + receiverNationalID, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Error swapping " + requestorNationalID + " and " + receiverNationalID, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/task/add")
	public ResponseEntity<String> addTask(@RequestParam String taskDay, @RequestParam String receiverNationalID) {
		boolean success = scheduleService.addTask(taskDay, receiverNationalID);
		if (success) {
			return new ResponseEntity<>("Added task for " + receiverNationalID, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Error adding task for " + receiverNationalID, HttpStatus.BAD_REQUEST);
		}
	}

	public static boolean getActive() {
		return active;
	}
}
