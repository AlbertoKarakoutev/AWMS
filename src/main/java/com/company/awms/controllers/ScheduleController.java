package com.company.awms.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.security.EmployeeDetails;
import com.company.awms.services.EmployeeService;
import com.company.awms.services.ScheduleService;

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
	public String viewSchedule(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam int month) {
		
		try {
			
			Employee authenticatedEmployee = this.employeeService.getEmployee(employeeDetails.getID());
			List<EmployeeDailyReference>[] sameLevelEmployees = this.scheduleService.viewSchedule(authenticatedEmployee.getDepartment(), authenticatedEmployee.getLevel(),month);
			model.addAttribute("sameLevelEmployees", sameLevelEmployees);
			model.addAttribute("month", month);
			injectEmailAndNameIntoModel(model, employeeDetails);

			return "schedule";
		} catch (IOException e) {
			return "badRequest";
		} catch (Exception e){
			e.printStackTrace();
			return "internalServerError";
		}
	}

	private void injectEmailAndNameIntoModel(Model model, EmployeeDetails employeeDetails){
		model.addAttribute("employeeName", employeeDetails.getFirstName() + " " + employeeDetails.getLastName());
		model.addAttribute("employeeEmail", employeeDetails.getUsername());
		model.addAttribute("employeeId", employeeDetails.getID());
	}

	public static boolean getActive() {
		return active;
	}
}
