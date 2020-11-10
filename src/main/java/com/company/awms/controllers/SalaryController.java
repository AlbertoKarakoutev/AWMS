package com.company.awms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.company.awms.services.SalaryService;

@RestController
public class SalaryController {

	private SalaryService salaryService;

	@Autowired
	public SalaryController(SalaryService salaryService) {
		this.salaryService = salaryService;
	}

	@GetMapping("/salary/{nationalID}")
	public ResponseEntity<String> getByName(@PathVariable String nationalID) {
		return new ResponseEntity<>(String.format("%3.2f hours of work this month", salaryService.calculateWorkHours(nationalID)), HttpStatus.OK);
	}
}
