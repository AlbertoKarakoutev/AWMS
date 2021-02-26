package com.company.awms.modules.base.schedule.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.company.awms.modules.base.employees.data.EmployeeDailyReference;

@Document
public class Day {
	
	@Id
	private String id;

	private LocalDate date;
	private List<EmployeeDailyReference> employees = new ArrayList<>();

	public Day () {}

	public Day(LocalDate date) {
		this.date = date;
	}

	public LocalDate getDate() {
		return date;
	}

	public List<EmployeeDailyReference> getEmployees() {
		return employees;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setEmployees(List<EmployeeDailyReference> employees) {
		this.employees = employees;
	}
	
	public void addEmployee(EmployeeDailyReference edr) {
		this.employees.add(edr);
	}

	public String getID() {
		return this.id;
	}
}
