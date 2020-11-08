package com.company.awms.data.schedule;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.employees.EmployeeRepo;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;

public class Day {

	public EmployeeRepo employeeRepo;

	@Id
	public String id;

	public String date;
	public ArrayList<EmployeeDailyReference> employees;

	// Should be in DD.MM.YYYY format
	public Day(String date) {
		this.date = date;
	}

	// Create a employee reference with appropriate information and add to the current day employees array
	public void addEmployee(String nationalID, int[] workTime) {
		Employee employee = employeeRepo.findByNationalID(nationalID);
		EmployeeDailyReference edr = employee.createEmployeeDailyReference(date, workTime);
		employees.add(edr);
	}

}
