package com.company.awms.controllers;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.security.EmployeeDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.company.awms.services.EmployeeService;
import com.company.awms.services.ScheduleService;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

	private static final boolean active = true;

	private ScheduleService scheduleService;
	private EmployeeService employeeService;

	@Autowired
	public ScheduleController(ScheduleService scheduleService, EmployeeService employeeService) {
		this.scheduleService = scheduleService;
		this.employeeService = employeeService;
	}

	@ResponseBody
	@GetMapping("/swap")
	public ResponseEntity<String> swapEmployees(@RequestParam String requestorNationalID, @RequestParam String receiverNationalID, @RequestParam String requestorDate, @RequestParam String receiverDate) {
		boolean success = scheduleService.swapEmployees(requestorNationalID, receiverNationalID, requestorDate, receiverDate);
		if (success) {
			return new ResponseEntity<String>("Successfully swapped " + requestorNationalID + " and " + receiverNationalID, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Error swapping " + requestorNationalID + " and " + receiverNationalID, HttpStatus.BAD_REQUEST);
		}
	}

	@ResponseBody
	@GetMapping("/task/add")
	public ResponseEntity<String> addTask(@RequestParam String taskDay, @RequestParam String receiverNationalID) {
		boolean success = scheduleService.addTask(taskDay, receiverNationalID);
		if (success) {
			return new ResponseEntity<>("Added task for " + receiverNationalID, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Error adding task for " + receiverNationalID, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("")
	public String viewSchedule(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			Employee authenticatedEmployee = this.employeeService.getEmployee(employeeDetails.getID());
			List<List<EmployeeDailyReference>> sameLevelEmployees = this.scheduleService.viewSchedule(authenticatedEmployee.getDepartment(), authenticatedEmployee.getLevel());

			model.addAttribute("sameLevelEmployees", sameLevelEmployees);

			return "calendar";
		} catch (IOException e) {
			return "badRequest";
		} catch (Exception e){
			e.printStackTrace();
			return "internalServerError";
		}
	}

	public static boolean getActive() {
		return active;
	}
}
