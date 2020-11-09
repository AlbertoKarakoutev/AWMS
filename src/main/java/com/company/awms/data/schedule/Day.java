package com.company.awms.data.schedule;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.employees.EmployeeRepo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

@Document
public class Day {
	
	@Id
	public String id;

	public LocalDate date;
	public ArrayList<EmployeeDailyReference> employees;

	// Should be in DD.MM.YYYY format
	public Day(LocalDate date) {
		this.date = date;
	}

	public LocalDate getDate() {
		return date;
	}

	public ArrayList<EmployeeDailyReference> getEmployees() {
		return employees;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setEmployees(ArrayList<EmployeeDailyReference> employees) {
		this.employees = employees;
	}
}
