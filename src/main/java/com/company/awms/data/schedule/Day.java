package com.company.awms.data.schedule;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.company.awms.data.employees.EmployeeDailyReference;

@Document
public class Day {
	
	@Id
	private String id;

	private LocalDate date;
	private ArrayList<EmployeeDailyReference> employees;

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

	public String getID() {
		return this.id;
	}
}
