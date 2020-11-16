package com.company.awms.controllers;

import java.io.FileReader;
import java.time.LocalDate;
import java.time.YearMonth;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.company.awms.data.schedule.Day;
import com.company.awms.services.EmployeeService;
import com.company.awms.services.ScheduleService;

@RestController
public class ScheduleController {

	ScheduleService scheduleService;
	EmployeeService employeeService;

	@Autowired
	public ScheduleController(ScheduleService scheduleService, EmployeeService employeeService) {
		this.scheduleService = scheduleService;
		this.employeeService = employeeService;
	}

	// Populate DB with a month's dates
	@GetMapping("/schedule/month/{month}")
	public String addDays(@PathVariable int month) {
		LocalDate now = LocalDate.now().withMonth(month);
		YearMonth yearMonthObject = YearMonth.of(now.getYear(), now.getMonthValue());
		for (int i = 1; i <= yearMonthObject.lengthOfMonth(); i++) {
			LocalDate correctDate = now.withDayOfMonth(i);
			Day day = new Day(correctDate);
			System.out.println(correctDate);
			ScheduleService.getRepository().save(day);
		}
		return "Done";
	}

	//Test Method
	//@GetMapping("/schedule/add/{day}")
	public ResponseEntity<String> addWorkDay(@PathVariable int day) {
		LocalDate date = LocalDate.now().withMonth(10).withDayOfMonth(day);
		JSONObject departments = null;
		try {
			departments = (JSONObject) new JSONParser().parse(new FileReader("src/main/resources/departments.json"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject thisDepartment = (JSONObject) departments.get("b");  
		boolean success = scheduleService.addWorkDay("5fa80775cb0e9c6301e92d3a", date, thisDepartment, 0);
		if(success) {
			return new ResponseEntity<String>("Successfully applied schedule!", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Error applying scheduole!", HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/schedule/apply")
	public ResponseEntity<String> applyRegularSchedule() {
		boolean success = scheduleService.applyRegularSchedule("b", 0);
		if(success) {
			return new ResponseEntity<String>("Successfully applied schedule!", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Error applying scheduole!", HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/schedule/applyIrregular")
	public ResponseEntity<String> applyIrregularSchedule() {
		boolean success = scheduleService.applyIrregularSchedule("b", 0);
		if(success) {
			return new ResponseEntity<String>("Successfully applied schedule!", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Error applying scheduole!", HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/schedule/swap/{requestorNationalID}/{receiverNationalID}/{requestorDate}/{receiverDate}")
	public ResponseEntity<String> swapEmployees(@PathVariable String requestorNationalID, @PathVariable String receiverNationalID,
			@PathVariable String requestorDate, @PathVariable String receiverDate) {
		boolean success = scheduleService.swapEmployees(requestorNationalID, receiverNationalID, requestorDate, receiverDate);
		if(success) {
			return new ResponseEntity<String>("Successfully swapped " + requestorNationalID + " and " + receiverNationalID, HttpStatus.OK);
		}else {
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
}
