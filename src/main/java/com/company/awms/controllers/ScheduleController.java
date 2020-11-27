package com.company.awms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.awms.services.EmployeeService;
import com.company.awms.services.ScheduleService;

@RestController
public class ScheduleController {

	private static final boolean active = true;

	private ScheduleService scheduleService;

	@Autowired
	public ScheduleController(ScheduleService scheduleService, EmployeeService employeeService) {
		this.scheduleService = scheduleService;
	}

	// Populate DB with a month's dates
	@GetMapping("/schedule/month/{month}")
	public String addDays(@PathVariable int month) {
		try {
			scheduleService.addMonthlyDays(month);
		} catch (Exception e) {
			System.out.println("Error");
			return "Error";
		}
		return "Done";
	}

	@GetMapping("/schedule/apply")
	public ResponseEntity<String> applyRegularSchedule() {
		boolean success = scheduleService.applyRegularSchedule("b", 0);
		if (success) {
			return new ResponseEntity<String>("Successfully applied regular schedule!", HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Error applying schedule!", HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/schedule/applyIrregular")
	public ResponseEntity<String> applyIrregularSchedule() {
		boolean success = scheduleService.applyIrregularSchedule("b", 0);
		if (success) {
			return new ResponseEntity<String>("Successfully applied irregular schedule!", HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Error applying schedule!", HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/schedule/applyOncall")
	public ResponseEntity<String> applyOnCallSchedule() {
		boolean success = scheduleService.applyOnCallSchedule("c", 0);
		if (success) {
			return new ResponseEntity<String>("Successfully applied on-call schedule!", HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Error applying schedule!", HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/schedule/swap")
	public ResponseEntity<String> swapEmployees(@RequestParam String requestorNationalID, @RequestParam String receiverNationalID, @RequestParam String requestorDate, @RequestParam String receiverDate) {
		boolean success = scheduleService.swapEmployees(requestorNationalID, receiverNationalID, requestorDate, receiverDate);
		if (success) {
			return new ResponseEntity<String>("Successfully swapped " + requestorNationalID + " and " + receiverNationalID, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Error swapping " + requestorNationalID + " and " + receiverNationalID, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/schedule/task/{taskDay}/{receiverNationalID}")
	public ResponseEntity<String> addTask(@PathVariable String taskDay, @PathVariable String receiverNationalID) {
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
