package com.company.awms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.awms.services.SalaryService;

@RestController
@RequestMapping("/salary")
public class SalaryController {

	private static boolean active = true;

	private SalaryService salaryService;

	@Autowired
	public SalaryController(SalaryService salaryService) {
		this.salaryService = salaryService;
	}

	@GetMapping("/workHours/{nationalID}")
	public ResponseEntity<String> getByName(@PathVariable String nationalID) {
		if (active) {
			try {
				double workHours = salaryService.calculateWorkHours(nationalID);
				return new ResponseEntity<>(String.format("%3.2f hours of work this month", workHours), HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/{nationalID}")
	public ResponseEntity<String> getSalary(@PathVariable String nationalID) {
		if (active) {
			try {
				double salary = salaryService.estimateSalary(nationalID, 1.0);
				return new ResponseEntity<>(String.format("Approximately %3.2f leva for this month", salary), HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public static boolean getActive() {
		return active;
	}
	
	public void setActive(boolean newActive) {
		active = newActive;
	}
}
