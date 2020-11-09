package com.company.awms.data.schedule;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.employees.EmployeeRepo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document
public class Day {

	public EmployeeRepo employeeRepo;
	public ScheduleRepo scheduleRepo;
	
	@Id
	public String id;

	public String date;
	public ArrayList<EmployeeDailyReference> employees;

	// Should be in DD.MM.YYYY format
	public Day(String date) {
		this.date = date;
	}

	// Create a employee reference with appropriate information and add to the
	// current day employees array
	public void addEmployee(String nationalID, int[] workTime) {
		Employee employee = employeeRepo.findByNationalID(nationalID);
		EmployeeDailyReference edr = employee.createEmployeeDailyReference(date, workTime);
		employees.add(edr);
	}

	public void swapEmployees(String requestorID, String receiverID, String requestorDate) {
		int workTime[];
		EmployeeDailyReference requestor;
		EmployeeDailyReference receiver;
		
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).id.equals(requestorID)) {
				workTime = employees.get(i).getWorkTime();
				requestor = employees.get(i);
			}
		}
		//employees.remove
	}
}
