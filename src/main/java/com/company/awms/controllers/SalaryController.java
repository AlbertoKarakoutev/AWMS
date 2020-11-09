package com.company.awms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@GetMapping("/salary/{lastName}")
	public double getByName(@PathVariable String lastName){

        return salaryService.calculateWorkHours(lastName);
    }
}
